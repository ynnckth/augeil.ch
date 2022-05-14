package ch.augeil.admin.album;

import ch.augeil.admin.album.downloadcodes.DownloadCode;
import ch.augeil.admin.album.downloadcodes.DownloadCodeGenerator;
import ch.augeil.admin.album.downloadcodes.DownloadCodeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// TODO: protect all endpoints behind a basic auth except for the download code redemption endpoint
@RestController
@RequestMapping("/albums")
public class AlbumController {

    private final Logger log = LoggerFactory.getLogger(AlbumController.class);
    private final DownloadCodeGenerator downloadCodeGenerator;
    private final AlbumRepository albumRepository;
    private final DownloadCodeRepository downloadCodeRepository;

    public AlbumController(DownloadCodeGenerator downloadCodeGenerator, AlbumRepository albumRepository, DownloadCodeRepository downloadCodeRepository) {
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

    @PostMapping(/*consumes = MediaType.MULTIPART_MIXED_VALUE*/)
    public ResponseEntity<Album> createAndUploadAlbum(
            // @RequestPart("albumZipFile") MultipartFile albumZipFile,
            @RequestParam("artist") String artist,
            @RequestParam("albumName") String albumName) {
        Album album = new Album();
        album.setArtist(artist);
        album.setAlbumName(albumName);
        // TODO: upload the provided album zip file to a file server (e.g. Azure blob storage or S3)
        //  and set the url to the download location
        //  https://docs.microsoft.com/en-us/azure/developer/java/spring-framework/configure-spring-boot-starter-java-app-with-azure-storage
        album.setFilePath("azure.com/myAlbum");
        Album savedAlbum = albumRepository.save(album);
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

    // TODO: return the album zip file in response (not the path to the download location!)
    @GetMapping("/{downloadCode}/redeem")
    public ResponseEntity<Object> redeemDownloadCode(@PathVariable String downloadCode) {
        log.info("Requested to redeem download code {}", downloadCode);
        return downloadCodeRepository.findById(downloadCode)
                .map(foundDownloadCode -> {
                    log.info("Decrementing available downloads for code {}", downloadCode);
                    foundDownloadCode.decrementAvailableDownloads();
                    log.info("Saving decremented download code {}", downloadCode);
                    downloadCodeRepository.save(foundDownloadCode);

                    Album matchingAlbum = albumRepository
                            .findById(foundDownloadCode.getAlbumId())
                            .orElseThrow(() -> new IllegalStateException(String.format("Album %s not found for download code %s", foundDownloadCode.getAlbumId(), downloadCode)));

                    // TODO: download zip file from: matchingAlbum.getFilePath() and return it
                    return ResponseEntity.ok().build();
                })
                .orElse(ResponseEntity.notFound().build());
    }
}
