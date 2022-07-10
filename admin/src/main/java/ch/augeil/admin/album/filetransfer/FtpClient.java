package ch.augeil.admin.album.filetransfer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.integration.ftp.session.FtpRemoteFileTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class FtpClient implements AlbumStorageService {

    private final Logger log = LoggerFactory.getLogger(FtpClient.class);

    @Value("${tempFileDownloadPath}")
    private String tempFileDownloadPath;
    private final FtpRemoteFileTemplate ftpFileTemplate;

    public FtpClient(FtpRemoteFileTemplate ftpFileTemplate) {
        this.ftpFileTemplate = ftpFileTemplate;
    }

    @Override
    public void uploadAlbum(MultipartFile albumZipFile) {
        ftpFileTemplate.execute(session -> {
            log.info("Uploading album zip file {} to FTP server", albumZipFile.getOriginalFilename());
            session.write(albumZipFile.getInputStream(), albumZipFile.getOriginalFilename());
            log.info("Successfully uploaded album zip file {} to FTP server", albumZipFile.getOriginalFilename());
            return null;
        });
    }

    @Override
    public ByteArrayResource downloadAlbum(String albumFileName) throws IOException {
        return ftpFileTemplate.execute(session -> {
            String tempFilePath = getLocalFileStorageLocation() + "/" + albumFileName;
            Files.deleteIfExists(Paths.get(tempFilePath));
            File outputFile = new File(tempFilePath);

            try (var fileOutputStream = new FileOutputStream(outputFile)) {
                log.info("Downloading album zip file {} from FTP server", albumFileName);
                session.read(albumFileName, fileOutputStream);
            }
            log.info("Successfully downloaded album zip file {} from FTP server", albumFileName);
            return new ByteArrayResource(Files.readAllBytes(Paths.get(tempFilePath)));
        });
    }

    @Override
    public Path getLocalFileStorageLocation() {
        return Path.of(tempFileDownloadPath);
    }
}
