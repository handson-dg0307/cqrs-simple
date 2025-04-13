package com.telecom.cqrs.command.config;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.telecom.cqrs.common.constant.Constants;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Slf4j
public class EventHubConfig {
    // 단일 ConnectionString으로 변경
    @Value("${event-hub.connection-string}")
    private String connectionString;

    @Value("${event-hub.hub-name}")
    private String eventHubName;

    @PostConstruct
    public void init() {
        log.info("Initializing EventHub configuration with hub name: {}", eventHubName);
    }

    // 단일 EventHubProducerClient 생성
    @Bean(name = "eventProducer")
    public EventHubProducerClient eventProducer() {
        log.info("Creating Event producer for hub: {}", eventHubName);
        return new EventHubClientBuilder()
                .connectionString(connectionString, eventHubName)
                .buildProducerClient();
    }
}
