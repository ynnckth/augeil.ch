package ch.augeil.admin.album;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class AlbumStorageService {

    private final Logger log = LoggerFactory.getLogger(AlbumStorageService.class);

    private final BlobContainerClient blobContainerClient;

    public AlbumStorageService(BlobContainerClient blobContainerClient) {
        this.blobContainerClient = blobContainerClient;
    }

    public void uploadAlbum(MultipartFile albumZipFile) throws IOException {
        String albumFileName = albumZipFile.getOriginalFilename();
        log.info("Uploading album file {} to azure blob storage", albumFileName);
        BlobClient blobClient = blobContainerClient.getBlobClient(albumFileName);
        blobClient.upload(albumZipFile.getInputStream(), albumZipFile.getSize(), true);
        log.info("Successfully uploaded album file {} to azure blob storage", albumFileName);
    }
}
