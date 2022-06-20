package ch.augeil.admin.configuration;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Getter
@Configuration
public class SftpConfiguration {

    @Value("${albumstorage.host}")
    private String sftpHost;

    @Value("${albumstorage.username}")
    private String sftpUsername;

    @Value("${albumstorage.password}")
    private String sftpPassword;

}
