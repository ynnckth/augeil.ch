package ch.augeil.admin.album;

import ch.augeil.admin.album.downloadcodes.DownloadCode;
import ch.augeil.admin.album.downloadcodes.DownloadCodeGenerator;
import ch.augeil.admin.album.downloadcodes.DownloadCodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

// TODO: protect all endpoints behind a basic auth except for the download code redemption endpoint
@RestController
@RequestMapping("/albums")
public class AlbumController {

    private final Logger log = LoggerFactory.getLogger(AlbumController.class);

    private final AlbumStorageService albumUploadService;
    private final AlbumRepository albumRepository;
    private final DownloadCodeRepository downloadCodeRepository;
    private final DownloadCodeGenerator downloadCodeGenerator;

    public AlbumController(AlbumStorageService albumStorageService, DownloadCodeGenerator downloadCodeGenerator, AlbumRepository albumRepository, DownloadCodeRepository downloadCodeRepository) {
        this.albumUploadService = albumStorageService;
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
        Album album = new Album();
        album.setArtist(artist);
        album.setAlbumName(albumName);
        album.setFileName(albumZipFile.getOriginalFilename());
        albumUploadService.uploadAlbum(albumZipFile);
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
                    log.info("Decrementing available downloads for code {}", downloadCode);
                    foundDownloadCode.decrementAvailableDownloads();
                    log.info("Saving decremented download code {}", downloadCode);
                    downloadCodeRepository.save(foundDownloadCode);
                    log.info("Saved updated download code {}", downloadCode);

                    Album matchingAlbum = albumRepository
                            .findById(foundDownloadCode.getAlbumId())
                            .orElseThrow(() -> new RuntimeException(String.format("Album %s not found for download code %s", foundDownloadCode.getAlbumId(), downloadCode)));

                    ByteArrayResource downloadedAlbumFile = albumUploadService.downloadAlbum(matchingAlbum.getFileName());
                    return ResponseEntity
                            .ok()
                            .contentLength(downloadedAlbumFile.contentLength())
                            .body(downloadedAlbumFile);
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
