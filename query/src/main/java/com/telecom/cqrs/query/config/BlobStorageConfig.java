package com.telecom.cqrs.query.config;

import com.azure.storage.blob.BlobContainerAsyncClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.annotation.Retryable;

@Slf4j
@Configuration
@EnableRetry
public class BlobStorageConfig {

    @Value("${azure.storage.connection-string}")
    private String storageConnectionString;

    @Value("${azure.storage.container}")
    private String containerName;

    private BlobServiceClient blobServiceClient;

    @PostConstruct
    public void init() {
        initBlobServiceClient();
        initializeContainers();
    }

    private void initBlobServiceClient() {
        this.blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(storageConnectionString)
                .buildClient();
        log.info("Blob service client initialized");
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000))
    private void initializeContainers() {
        try {
            // 단일 컨테이너 생성
            createContainerIfNotExists(containerName);
            log.info("Blob container initialized successfully");
        } catch (Exception e) {
            log.error("Failed to initialize blob container: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to initialize storage", e);
        }
    }

    public void createContainerIfNotExists(String containerName) {
        try {
            BlobContainerClient containerClient = blobServiceClient.getBlobContainerClient(containerName);
            if (!containerClient.exists()) {
                containerClient.create();
                log.info("Created blob container: {}", containerName);
            } else {
                log.info("Blob container already exists: {}", containerName);
            }
        } catch (Exception e) {
            log.error("Error creating blob container {}: {}", containerName, e.getMessage());
            throw e;
        }
    }

    public BlobContainerAsyncClient getBlobContainerAsyncClient(String containerName) {
        createContainerIfNotExists(containerName);
        return new BlobServiceClientBuilder()
                .connectionString(storageConnectionString)
                .buildAsyncClient()
                .getBlobContainerAsyncClient(containerName);
    }

    public BlobContainerClient getBlobContainerClient(String containerName) {
        createContainerIfNotExists(containerName);
        return blobServiceClient.getBlobContainerClient(containerName);
    }
}
