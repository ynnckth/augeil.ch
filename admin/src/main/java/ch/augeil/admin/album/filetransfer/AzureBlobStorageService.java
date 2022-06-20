package ch.augeil.admin.album.filetransfer;

import ch.augeil.admin.configuration.AzureBlobStorageConfiguration;
import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class AzureBlobStorageService implements AlbumStorageService {

    private final Logger log = LoggerFactory.getLogger(AzureBlobStorageService.class);

    private final BlobContainerClient blobContainerClient;
    private final Path localFileStorageLocation;

    public AzureBlobStorageService(AzureBlobStorageConfiguration blobStorageConfiguration, BlobContainerClient blobContainerClient) {
        this.blobContainerClient = blobContainerClient;
        this.localFileStorageLocation = Path.of(blobStorageConfiguration.getTempFileDownloadPath());
    }

    @Override
    public void uploadAlbum(MultipartFile albumZipFile) throws IOException {
        String albumFileName = albumZipFile.getOriginalFilename();
        log.info("Uploading album file {} to azure blob storage", albumFileName);
        BlobClient blobClient = blobContainerClient.getBlobClient(albumFileName);
        blobClient.upload(albumZipFile.getInputStream(), albumZipFile.getSize(), true);
        log.info("Successfully uploaded album file {} to azure blob storage", albumFileName);
    }

    @Override
    public ByteArrayResource downloadAlbum(String albumFileName) {
        log.info("Downloading album file {} from azure blob storage", albumFileName);
        BlobClient blobClient = blobContainerClient.getBlobClient(albumFileName);

        String tempFilePath = localFileStorageLocation + "/" + albumFileName;
        try {
            Files.deleteIfExists(Paths.get(tempFilePath));
            blobClient.downloadToFile(new File(tempFilePath).getPath());
            log.info("Downloaded album file {} from azure blob storage to temporary local file {}", albumFileName, tempFilePath);
            return new ByteArrayResource(Files.readAllBytes(Paths.get(tempFilePath)));
        } catch (IOException e) {
            log.error("Failed to download album {}", albumFileName, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Path getLocalFileStorageLocation() {
        return localFileStorageLocation;
    }
}
