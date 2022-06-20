package ch.augeil.admin.album.filetransfer;

import ch.augeil.admin.configuration.SftpConfiguration;
import org.apache.commons.net.ftp.FTP;
import org.apache.commons.net.ftp.FTPClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class SftpStorageService implements AlbumStorageService {

    private final Logger log = LoggerFactory.getLogger(SftpStorageService.class);

    private final SftpConfiguration sftpConfiguration;
    private final FTPClient ftpClient;

    @Value("${tempFileDownloadPath}")
    private String tempFileDownloadPath;

    public SftpStorageService(SftpConfiguration sftpConfiguration, FTPClient ftpClient) {
        this.sftpConfiguration = sftpConfiguration;
        this.ftpClient = ftpClient;
    }

    @Override
    public void uploadAlbum(MultipartFile albumZipFile) throws IOException {
        connectAndLoginToFtpServer();
        log.info("Uploading album zip file {} to FTP server", albumZipFile.getOriginalFilename());
        boolean result = ftpClient.storeFile(albumZipFile.getOriginalFilename(), albumZipFile.getInputStream());
        if (!result) {
            throw new IllegalStateException("Failed to upload album zip file to FTP server");
        }
        log.info("Successfully uploaded album zip file {} to FTP server", albumZipFile.getOriginalFilename());
        ftpClient.logout();
        ftpClient.disconnect();
        log.info("Disconnected from FTP server");
    }

    @Override
    public ByteArrayResource downloadAlbum(String albumFileName) throws IOException {
        connectAndLoginToFtpServer();
        log.info("Downloading album zip file {} from FTP server", albumFileName);
        String tempFilePath = getLocalFileStorageLocation() + "/" + albumFileName;
        Files.deleteIfExists(Paths.get(tempFilePath));
        File outputFile = new File(tempFilePath);
        FileOutputStream fileOutputStream = new FileOutputStream(outputFile);
        boolean result = ftpClient.retrieveFile(albumFileName, fileOutputStream);
        fileOutputStream.flush();
        fileOutputStream.close();
        if (!result) {
            throw new IllegalStateException("Failed to download album zip file from FTP server");
        }
        log.info("Successfully downloaded album zip file {} from FTP server", albumFileName);
        ftpClient.logout();
        ftpClient.disconnect();
        log.info("Disconnected from FTP server");
        return new ByteArrayResource(Files.readAllBytes(Paths.get(tempFilePath)));
    }

    private void connectAndLoginToFtpServer() throws IOException {
        ftpClient.connect(sftpConfiguration.getSftpHost());
        log.info("Connected to FTP server {}", sftpConfiguration.getSftpHost());
        if (!ftpClient.login(sftpConfiguration.getSftpUsername(), sftpConfiguration.getSftpPassword())) {
            throw new IllegalStateException("Failed to login to FTP server");
        }
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
    }

    @Override
    public Path getLocalFileStorageLocation() {
        return Path.of(tempFileDownloadPath);
    }
}
