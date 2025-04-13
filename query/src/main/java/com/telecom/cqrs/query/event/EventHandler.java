package com.telecom.cqrs.query.event;

import com.azure.messaging.eventhubs.EventProcessorClient;
import com.azure.messaging.eventhubs.models.ErrorContext;
import com.azure.messaging.eventhubs.models.EventContext;
import com.azure.messaging.eventhubs.EventData;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telecom.cqrs.common.constant.EventHubConstants;
import com.telecom.cqrs.common.event.PhonePlanEvent;
import com.telecom.cqrs.common.event.UsageUpdatedEvent;
import com.telecom.cqrs.query.domain.UsageView;
import com.telecom.cqrs.query.exception.EventProcessingException;
import com.telecom.cqrs.query.repository.UsageViewRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.retry.annotation.Retryable;
import org.springframework.retry.annotation.Backoff;

import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Consumer;

@Slf4j
@Service
public class EventHandler implements Consumer<EventContext> {
    private final UsageViewRepository usageViewRepository;
    private final ObjectMapper objectMapper;
    private final RetryTemplate retryTemplate;
    private final AtomicLong eventsProcessed = new AtomicLong(0);
    private final AtomicLong eventErrors = new AtomicLong(0);
    private EventProcessorClient eventProcessorClient;

    public EventHandler(
            UsageViewRepository usageViewRepository,
            ObjectMapper objectMapper,
            RetryTemplate retryTemplate) {
        this.usageViewRepository = usageViewRepository;
        this.objectMapper = objectMapper;
        this.retryTemplate = retryTemplate;
    }

    public void setEventProcessorClient(EventProcessorClient client) {
        log.info("Setting Event Processor Client");
        this.eventProcessorClient = client;
        startEventProcessing();
    }

    @Retryable(maxAttempts = 3, backoff = @Backoff(delay = 1000, multiplier = 2))
    public void startEventProcessing() {
        if (eventProcessorClient != null) {
            log.info("Starting Event Processor...");
            try {
                eventProcessorClient.start();
                log.info("Event Processor started successfully");
            } catch (Exception e) {
                log.error("Failed to start Event Processor: {}", e.getMessage(), e);
                throw new RuntimeException("Failed to start event processor", e);
            }
        } else {
            log.warn("Event Processor Client is not set");
        }
    }

    @Override
    @Transactional
    public void accept(EventContext eventContext) {
        EventData eventData = eventContext.getEventData();
        String eventBody = eventData.getBodyAsString();
        String partitionId = eventContext.getPartitionContext().getPartitionId();
        Map<String, Object> properties = eventData.getProperties();
        
        String topic = (String) properties.get(EventHubConstants.PROPERTY_TOPIC);
        String eventType = (String) properties.get(EventHubConstants.PROPERTY_TYPE);
        
        log.info("Received event: topic={}, type={}, partition={}", topic, eventType, partitionId);

        try {
            // 이벤트 타입에 따라 처리 로직 분기
            if (EventHubConstants.TOPIC_PLAN.equals(topic) && 
                EventHubConstants.EVENT_TYPE_PLAN.equals(eventType)) {
                processPlanEvent(eventBody);
            } else if (EventHubConstants.TOPIC_USAGE.equals(topic) && 
                       EventHubConstants.EVENT_TYPE_USAGE.equals(eventType)) {
                processUsageEvent(eventBody);
            } else {
                log.warn("Unknown event type received: topic={}, type={}", topic, eventType);
            }
            
            eventsProcessed.incrementAndGet();
            eventContext.updateCheckpoint();
        } catch (Exception e) {
            log.error("Failed to process event: topic={}, type={}, partition={}, error={}", 
                    topic, eventType, partitionId, e.getMessage(), e);
            eventErrors.incrementAndGet();
        }
    }

    private void processPlanEvent(String eventBody) {
        try {
            PhonePlanEvent event = objectMapper.readValue(eventBody, PhonePlanEvent.class);
            log.debug("Processing plan event for userId={}", event.getUserId());
            
            retryTemplate.execute(context -> {
                UsageView view = getOrCreatePhonePlanView(event.getUserId());
                updateViewFromPlanEvent(view, event);
                UsageView savedView = usageViewRepository.save(view);
                log.info("Plan event processed: userId={}, planName={}, dataAllowance={}", 
                        savedView.getUserId(), savedView.getPlanName(), savedView.getDataAllowance());
                return null;
            });
        } catch (Exception e) {
            log.error("Error processing plan event: {}", e.getMessage(), e);
            throw new EventProcessingException("Failed to process plan event", e);
        }
    }

    private void processUsageEvent(String eventBody) {
        try {
            UsageUpdatedEvent event = objectMapper.readValue(eventBody, UsageUpdatedEvent.class);
            log.debug("Processing usage event for userId={}", event.getUserId());
            
            retryTemplate.execute(context -> {
                UsageView view = usageViewRepository.findByUserId(event.getUserId());
                if (view != null) {
                    updateViewFromUsageEvent(view, event);
                    UsageView savedView = usageViewRepository.save(view);
                    log.info("Usage event processed: userId={}, dataUsage={}, callUsage={}", 
                            savedView.getUserId(), savedView.getDataUsage(), savedView.getCallUsage());
                } else {
                    log.warn("No UsageView found for userId={}, skipping usage update", event.getUserId());
                }
                return null;
            });
        } catch (Exception e) {
            log.error("Error processing usage event: {}", e.getMessage(), e);
            throw new EventProcessingException("Failed to process usage event", e);
        }
    }

    private UsageView getOrCreatePhonePlanView(String userId) {
        UsageView view = usageViewRepository.findByUserId(userId);
        if (view == null) {
            view = new UsageView();
            view.setUserId(userId);
            log.debug("Creating new UsageView for userId={}", userId);
        }
        return view;
    }

    private void updateViewFromPlanEvent(UsageView view, PhonePlanEvent event) {
        view.setPlanName(event.getPlanName());
        view.setDataAllowance(event.getDataAllowance());
        view.setCallMinutes(event.getCallMinutes());
        view.setMessageCount(event.getMessageCount());
        view.setMonthlyFee(event.getMonthlyFee());
        view.setStatus(event.getStatus());
    }

    private void updateViewFromUsageEvent(UsageView view, UsageUpdatedEvent event) {
        if (event.getDataUsage() != null) {
            view.setDataUsage(event.getDataUsage());
        }
        if (event.getCallUsage() != null) {
            view.setCallUsage(event.getCallUsage());
        }
        if (event.getMessageUsage() != null) {
            view.setMessageUsage(event.getMessageUsage());
        }
    }

    public void processError(ErrorContext errorContext) {
        log.error("Error in event processor: {}, {}",
                errorContext.getThrowable().getMessage(),
                errorContext.getPartitionContext().getPartitionId(),
                errorContext.getThrowable());
        eventErrors.incrementAndGet();
    }

    public long getProcessedEventCount() {
        return eventsProcessed.get();
    }

    public long getErrorCount() {
        return eventErrors.get();
    }

    public void cleanup() {
        if (eventProcessorClient != null) {
            try {
                log.info("Stopping Event Processor...");
                eventProcessorClient.stop();
                log.info("Event Processor stopped");
            } catch (Exception e) {
                log.error("Error stopping Event Processor: {}", e.getMessage(), e);
            }
        }
    }
}
