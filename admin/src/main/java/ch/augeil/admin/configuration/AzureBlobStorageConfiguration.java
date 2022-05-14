package ch.augeil.admin.configuration;

import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobContainerClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AzureBlobStorageConfiguration {

    @Value("${spring.cloud.azure.storage.blob.account-name}")
    private String storageAccountName;

    @Value("${spring.cloud.azure.storage.blob.account-key}")
    private String storageAccountKey;

    @Value("${spring.cloud.azure.storage.blob.container-name}")
    private String storageContainerName;

    @Bean
    public BlobContainerClient getBlobContainerClient() {
        String connectionString = String.format("AccountName=%s;AccountKey=%s;EndpointSuffix=core.windows.net;DefaultEndpointsProtocol=https;",
                storageAccountName, storageAccountKey);
        return new BlobContainerClientBuilder()
                .connectionString(connectionString)
                .containerName(storageContainerName)
                .buildClient();
    }
}
