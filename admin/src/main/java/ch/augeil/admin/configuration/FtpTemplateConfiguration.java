package ch.augeil.admin.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.ftp.session.DefaultFtpSessionFactory;
import org.springframework.integration.ftp.session.FtpRemoteFileTemplate;

@Configuration
public class FtpTemplateConfiguration {

    @Bean
    DefaultFtpSessionFactory defaultFtpSessionFactory(
            @Value("${ftp.username}") String username,
            @Value("${ftp.password}") String password,
            @Value("${ftp.host}") String host,
            @Value("${ftp.port}") int port) {
        DefaultFtpSessionFactory defaultFtpSessionFactory = new DefaultFtpSessionFactory();
        defaultFtpSessionFactory.setPassword(password);
        defaultFtpSessionFactory.setUsername(username);
        defaultFtpSessionFactory.setHost(host);
        defaultFtpSessionFactory.setPort(port);
        return defaultFtpSessionFactory;
    }

    @Bean
    FtpRemoteFileTemplate ftpRemoteFileTemplate(DefaultFtpSessionFactory defaultFtpSessionFactory) {
        return new FtpRemoteFileTemplate(defaultFtpSessionFactory);
    }
}
