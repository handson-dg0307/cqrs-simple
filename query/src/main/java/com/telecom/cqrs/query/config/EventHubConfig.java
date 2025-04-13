package com.telecom.cqrs.query.config;

import com.azure.messaging.eventhubs.EventProcessorClient;
import com.azure.messaging.eventhubs.EventProcessorClientBuilder;
import com.azure.messaging.eventhubs.checkpointstore.blob.BlobCheckpointStore;
import com.telecom.cqrs.query.event.EventHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import jakarta.annotation.PostConstruct;

@Slf4j
@Configuration
public class EventHubConfig {
    @Value("${eventhub.connection-string}")
    private String connectionString;

    @Value("${eventhub.hub-name}")
    private String eventHubName;

    @Value("${eventhub.consumer-group:$Default}")
    private String consumerGroup;

    @Value("${azure.storage.container}")
    private String blobContainer;

    private final BlobStorageConfig blobStorageConfig;
    private final EventHandler eventHandler;
    private final EventHubProperties eventHubProperties;

    public EventHubConfig(
            BlobStorageConfig blobStorageConfig,
            EventHandler eventHandler,
            EventHubProperties eventHubProperties) {
        this.blobStorageConfig = blobStorageConfig;
        this.eventHandler = eventHandler;
        this.eventHubProperties = eventHubProperties;
    }

    @PostConstruct
    public void validateConfig() {
        log.info("Validating Event Hub configuration...");
        validateConnectionString("connectionString", connectionString);
        validateNotEmpty("eventHubName", eventHubName);
        log.info("Event Hub configuration validated successfully");
    }

    private void validateConnectionString(String name, String connectionString) {
        if (connectionString == null || connectionString.trim().isEmpty()
                || !connectionString.contains("Endpoint=")
                || !connectionString.contains("SharedAccessKeyName=")) {
            throw new IllegalStateException(
                    String.format("Invalid Event Hub connection string format for %s", name));
        }
    }

    private void validateNotEmpty(String name, String value) {
        if (value == null || value.trim().isEmpty()) {
            throw new IllegalStateException(
                    String.format("Event Hub configuration error: %s is not configured", name));
        }
    }

    @Bean
    public EventProcessorClient eventProcessor() {
        log.info("Creating event processor with hub: {}, consumer group: {}, container: {}",
                eventHubName, consumerGroup, blobContainer);

        var blobClient = blobStorageConfig
                .getBlobContainerAsyncClient(blobContainer);

        EventProcessorClient client = new EventProcessorClientBuilder()
                .connectionString(connectionString, eventHubName)
                .consumerGroup(consumerGroup)
                .checkpointStore(new BlobCheckpointStore(blobClient))
                .processEvent(eventHandler)
                .processError(eventHandler::processError)
                .buildEventProcessorClient();

        eventHandler.setEventProcessorClient(client);
        return client;
    }
}
