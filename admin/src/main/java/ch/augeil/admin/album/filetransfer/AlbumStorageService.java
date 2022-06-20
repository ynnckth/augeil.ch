package ch.augeil.admin.album.filetransfer;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Path;

public interface AlbumStorageService {
    void uploadAlbum(MultipartFile albumZipFile) throws Exception;
    ByteArrayResource downloadAlbum(String albumFileName);
    Path getLocalFileStorageLocation();
}
