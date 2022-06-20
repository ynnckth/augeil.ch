package ch.augeil.admin.album;

import ch.augeil.admin.album.downloadcodes.DownloadCode;
import ch.augeil.admin.album.downloadcodes.DownloadCodeGenerator;
import ch.augeil.admin.album.downloadcodes.DownloadCodeRepository;
import ch.augeil.admin.album.filetransfer.SftpStorageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.springframework.http.HttpHeaders.CONTENT_DISPOSITION;

@RestController
@RequestMapping("/albums")
public class AlbumController {

    private final Logger log = LoggerFactory.getLogger(AlbumController.class);
    private static final String ZIP_CONTENT_TYPE = "application/zip";
    private static final long MAX_FILE_SIZE_IN_BYTES = 52428800L * 2; // 100MB

    private final SftpStorageService albumStorageService;
    private final AlbumRepository albumRepository;
    private final DownloadCodeRepository downloadCodeRepository;
    private final DownloadCodeGenerator downloadCodeGenerator;

    public AlbumController(SftpStorageService albumStorageService, DownloadCodeGenerator downloadCodeGenerator, AlbumRepository albumRepository, DownloadCodeRepository downloadCodeRepository) {
        this.albumStorageService = albumStorageService;
        this.downloadCodeGenerator = downloadCodeGenerator;
        this.albumRepository = albumRepository;
        this.downloadCodeRepository = downloadCodeRepository;
    }

    @GetMapping
    public ResponseEntity<List<Album>> getAlbums() {
        log.info("Requested all albums");
        List<Album> albums = albumRepository.findAll();
        albums.forEach(album -> {
            List<DownloadCode> downloadCodes = downloadCodeRepository.findAllByAlbumId(album.getId());
            album.setDownloadCodes(downloadCodes);
        });
        return ResponseEntity.ok(albums);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Album> getAlbum(@PathVariable String id) {
        log.info("Requested album {}", id);
        return albumRepository.findById(id)
                .map(album -> {
                    List<DownloadCode> downloadCodes = downloadCodeRepository.findAllByAlbumId(album.getId());
                    album.setDownloadCodes(downloadCodes);
                    return ResponseEntity.ok(album);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Album> createAndUploadAlbum(
            @RequestPart("albumZipFile") MultipartFile albumZipFile,
            @RequestParam("artist") String artist,
            @RequestParam("albumName") String albumName) throws IOException {
        if (!ZIP_CONTENT_TYPE.equals(albumZipFile.getContentType())) {
            log.warn("Attempted to upload non-zip file");
            return ResponseEntity.badRequest().build();
        }
        if (albumZipFile.getSize() > MAX_FILE_SIZE_IN_BYTES) {
            log.warn("Attempted to upload too large file {}", albumZipFile.getSize());
            return ResponseEntity.badRequest().build();
        }
        Album album = new Album();
        album.setArtist(artist);
        album.setAlbumName(albumName);
        album.setFileName(albumZipFile.getOriginalFilename());
        albumStorageService.uploadAlbum(albumZipFile);
        Album savedAlbum = albumRepository.save(album);
        log.info("Saved new album {}", savedAlbum.getId());
        return ResponseEntity.ok(savedAlbum);
    }

    @PostMapping("{id}/downloadcodes")
    public ResponseEntity<List<DownloadCode>> generateDownloadCodes(@PathVariable String id, @RequestParam int numberOfDownloadCodesToGenerate) {
        log.info("Generating {} download codes for album {} ...", numberOfDownloadCodesToGenerate, id);
        List<DownloadCode> downloadCodes = downloadCodeGenerator.generate(numberOfDownloadCodesToGenerate, id);
        log.info("Persisting generated download codes ...");
        List<DownloadCode> savedDownloadCodes = downloadCodeRepository.saveAll(downloadCodes);
        return ResponseEntity.ok(savedDownloadCodes);
    }

    @GetMapping("/{downloadCode}/redeem")
    public ResponseEntity<ByteArrayResource> redeemDownloadCode(@PathVariable String downloadCode) {
        log.info("Requested to redeem download code {}", downloadCode);
        return downloadCodeRepository.findById(downloadCode)
                .map(foundDownloadCode -> {
                    Album matchingAlbum = albumRepository
                            .findById(foundDownloadCode.getAlbumId())
                            .orElseThrow(() -> new RuntimeException(String.format("Album %s not found for download code %s", foundDownloadCode.getAlbumId(), downloadCode)));

                    ByteArrayResource downloadedAlbumFile = null;
                    try {
                        downloadedAlbumFile = albumStorageService.downloadAlbum(matchingAlbum.getFileName());
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to download album", e);
                    }
                    deleteTemporaryAlbumFile(matchingAlbum.getFileName());

                    log.info("Decrementing available downloads for code {}", downloadCode);
                    foundDownloadCode.decrementAvailableDownloads();
                    log.info("Saving decremented download code {}", downloadCode);
                    downloadCodeRepository.save(foundDownloadCode);
                    log.info("Saved updated download code {}", downloadCode);

                    return ResponseEntity
                            .ok()
                            .header(CONTENT_DISPOSITION,"attachment;filename=\"" + matchingAlbum.getFileName())
                            .contentLength(downloadedAlbumFile.contentLength())
                            .contentType(MediaType.valueOf(ZIP_CONTENT_TYPE))
                            .body(downloadedAlbumFile);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    private void deleteTemporaryAlbumFile(String albumFileName) {
        Path temporaryAlbumFile = Paths.get(albumStorageService.getLocalFileStorageLocation().toString(), albumFileName);
        try {
            log.info("Deleting temporary album file {} ...", temporaryAlbumFile);
            Files.delete(temporaryAlbumFile);
            log.info("Deleted temporary album file {}", temporaryAlbumFile);
        } catch (IOException e) {
            throw new RuntimeException("Failed to delete " + temporaryAlbumFile);
        }
    }
}
