package com.telecom.cqrs.command.service;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventDataBatch;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import com.azure.messaging.eventhubs.models.CreateBatchOptions;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.telecom.cqrs.command.domain.PhonePlan;
import com.telecom.cqrs.common.constant.EventHubConstants;
import com.telecom.cqrs.common.dto.UsageUpdateRequest;
import com.telecom.cqrs.common.dto.UsageUpdateResponse;
import com.telecom.cqrs.common.event.PhonePlanEvent;
import com.telecom.cqrs.common.event.UsageUpdatedEvent;
import com.telecom.cqrs.common.exception.EventHubException;
import com.telecom.cqrs.common.exception.PhonePlanChangeException;
import com.telecom.cqrs.common.exception.UsageUpdateException;
import com.telecom.cqrs.command.repository.PhonePlanRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Slf4j
@Service
public class PhonePlanCommandService {
    private final PhonePlanRepository phonePlanRepository;
    private final EventHubProducerClient eventProducer;
    private final ObjectMapper objectMapper;

    public PhonePlanCommandService(
            PhonePlanRepository phonePlanRepository,
            @Qualifier("eventProducer") EventHubProducerClient eventProducer,
            ObjectMapper objectMapper) {
        this.phonePlanRepository = phonePlanRepository;
        this.eventProducer = eventProducer;
        this.objectMapper = objectMapper;
    }

    public PhonePlan changePhonePlan(PhonePlan phonePlan) {
        try {
            PhonePlan savedPlan = savePlan(phonePlan);
            PhonePlanEvent event = createPlanEvent(savedPlan);
            // 단일 EventHub에 이벤트 발행 (토픽과 타입 속성 추가)
            publishEvent(event, savedPlan.getUserId(), EventHubConstants.TOPIC_PLAN, EventHubConstants.EVENT_TYPE_PLAN);
            return savedPlan;
        } catch (Exception e) {
            log.warn("요금제 변경 실패: userId={}, error={}", maskUserId(phonePlan.getUserId()), e.getMessage());
            throw new PhonePlanChangeException("요금제 변경 중 오류가 발생했습니다", e);
        }
    }

    // 이벤트 발행 메서드 - 토픽과 이벤트 타입을 파라미터로 받음
    private <T> void publishEvent(T event, String partitionKey, String topic, String eventType) {
        try {
            CreateBatchOptions options = new CreateBatchOptions()
                    .setPartitionKey(partitionKey);

            EventDataBatch batch = eventProducer.createBatch(options);
            String eventJson = objectMapper.writeValueAsString(event);
            EventData eventData = new EventData(eventJson);

            // 이벤트 속성 설정
            eventData.getProperties().put(EventHubConstants.PROPERTY_TYPE, eventType);
            eventData.getProperties().put(EventHubConstants.PROPERTY_TOPIC, topic);

            if (!batch.tryAdd(eventData)) {
                throw new EventHubException("이벤트 크기가 너무 큽니다");
            }

            eventProducer.send(batch);
            log.info("이벤트 발행 완료: topic={}, type={}, userId={}", 
                    topic, eventType, maskUserId(partitionKey));

        } catch (Exception e) {
            log.warn("이벤트 발행 실패: topic={}, type={}, userId={}, error={}", 
                    topic, eventType, maskUserId(partitionKey), e.getMessage());
            throw new EventHubException("이벤트 발행 중 오류가 발생했습니다", e);
        }
    }

    public UsageUpdateResponse updateUsage(UsageUpdateRequest request) {
        try {
            phonePlanRepository.findByUserId(request.getUserId())
                    .orElseThrow(() -> new UsageUpdateException("존재하지 않는 사용자입니다: " + maskUserId(request.getUserId())));

            UsageUpdatedEvent event = createUsageEvent(request);
            // 단일 EventHub에 이벤트 발행 (토픽과 타입 속성 추가)
            publishEvent(event, request.getUserId(), EventHubConstants.TOPIC_USAGE, EventHubConstants.EVENT_TYPE_USAGE);

            return UsageUpdateResponse.builder()
                    .success(true)
                    .message("사용량 업데이트가 완료되었습니다")
                    .userId(maskUserId(request.getUserId()))
                    .build();
        } catch (Exception e) {
            log.warn("사용량 업데이트 실패: userId={}, error={}", maskUserId(request.getUserId()), e.getMessage());
            throw new UsageUpdateException("사용량 업데이트 중 오류가 발생했습니다", e);
        }
    }

    private void update(PhonePlan existingPlan, PhonePlan newPlan) {
        existingPlan.setPlanName(newPlan.getPlanName());
        existingPlan.setDataAllowance(newPlan.getDataAllowance());
        existingPlan.setCallMinutes(newPlan.getCallMinutes());
        existingPlan.setMessageCount(newPlan.getMessageCount());
        existingPlan.setMonthlyFee(newPlan.getMonthlyFee());
        existingPlan.setStatus(newPlan.getStatus() == null ? existingPlan.getStatus() : newPlan.getStatus());
    }

    private PhonePlanEvent createPlanEvent(PhonePlan plan) {
        return PhonePlanEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(EventHubConstants.EVENT_TYPE_PLAN)
                .userId(plan.getUserId())
                .planName(plan.getPlanName())
                .dataAllowance(plan.getDataAllowance())
                .callMinutes(plan.getCallMinutes())
                .messageCount(plan.getMessageCount())
                .monthlyFee(plan.getMonthlyFee())
                .status(plan.getStatus())
                .timestamp(LocalDateTime.now())
                .build();
    }

    private UsageUpdatedEvent createUsageEvent(UsageUpdateRequest request) {
        return UsageUpdatedEvent.builder()
                .eventId(UUID.randomUUID().toString())
                .eventType(EventHubConstants.EVENT_TYPE_USAGE)
                .userId(request.getUserId())
                .dataUsage(request.getDataUsage())
                .callUsage(request.getCallUsage())
                .messageUsage(request.getMessageUsage())
                .timestamp(LocalDateTime.now())
                .build();
    }

    @Transactional
    protected PhonePlan savePlan(PhonePlan phonePlan) {
        return phonePlanRepository.findByUserId(phonePlan.getUserId())
                .map(existingPlan -> {
                    update(existingPlan, phonePlan);
                    return phonePlanRepository.save(existingPlan);
                })
                .orElseGet(() -> phonePlanRepository.save(phonePlan));
    }

    private String maskUserId(String userId) {
        if (userId == null || userId.isEmpty()) {
            return userId;
        }
        return userId.replaceAll("(\\w{2})(\\w+)(\\w{2})", "$1****$3");
    }
}
