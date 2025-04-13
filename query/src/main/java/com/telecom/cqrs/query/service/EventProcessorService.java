package com.telecom.cqrs.query.service;

import com.azure.messaging.eventhubs.EventProcessorClient;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;

@Slf4j
@Service
public class EventProcessorService {
    private final EventProcessorClient eventProcessor;

    public EventProcessorService(EventProcessorClient eventProcessor) {
        this.eventProcessor = eventProcessor;
    }

    @PostConstruct
    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void startProcessors() {
        try {
            log.info("Starting event processor...");
            eventProcessor.start();
            log.info("Event Processor started successfully");
        } catch (Exception e) {
            log.error("Failed to start event processor: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to start event processor", e);
        }
    }

    @PreDestroy
    public void stopProcessors() {
        try {
            log.info("Stopping event processor...");
            if (eventProcessor != null) {
                eventProcessor.stop();
                log.info("Event Processor stopped successfully");
            }
        } catch (Exception e) {
            log.error("Error stopping event processor: {}", e.getMessage(), e);
        }
    }
}
