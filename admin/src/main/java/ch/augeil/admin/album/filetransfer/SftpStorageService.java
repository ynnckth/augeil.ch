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

import java.io.IOException;
import java.nio.file.Path;

@Service
public class SftpStorageService implements AlbumStorageService {

    private final Logger log = LoggerFactory.getLogger(SftpStorageService.class);
    private final SftpConfiguration sftpConfiguration;

    @Value("${tempFileDownloadPath}")
    private String tempFileDownloadPath;

    public SftpStorageService(SftpConfiguration sftpConfiguration) {
        this.sftpConfiguration = sftpConfiguration;
    }

    @Override
    public void uploadAlbum(MultipartFile albumZipFile) throws IOException {
        FTPClient ftpClient = new FTPClient();
        ftpClient.connect(sftpConfiguration.getSftpHost());
        log.info("Connected to FTP server {}", sftpConfiguration.getSftpHost());

        if (!ftpClient.login(sftpConfiguration.getSftpUsername(), sftpConfiguration.getSftpPassword())) {
            throw new IllegalStateException("Failed to login to FTP server");
        }
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTP.BINARY_FILE_TYPE);
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
    public ByteArrayResource downloadAlbum(String albumFileName) {
        // TODO: implement download
        /*
        ChannelSftp channelSftp = null;
        OutputStream outputStream;
        try {
            channelSftp = createChannelSftp();
            File file = new File(localFilePath);
            outputStream = new FileOutputStream(file);
            channelSftp.get(remoteFilePath, outputStream);
            file.createNewFile();
            return new ByteArrayResource(Files.readAllBytes(Paths.get(tempFilePath)));
        } catch (JSchException e) {
            log.error("Failed to create sftp channel", e);
        } catch (SftpException | IOException e) {
            log.error("Failed to download file", e);
        } finally {
            disconnectChannelSftp(channelSftp);
        }
         */
        return null;
    }

    @Override
    public Path getLocalFileStorageLocation() {
        return Path.of(tempFileDownloadPath);
    }
}
