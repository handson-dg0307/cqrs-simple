package com.telecom.cqrs.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 사용량 업데이트 이벤트를 표현하는 클래스입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UsageUpdatedEvent {
    private String eventId;
    private String eventType;
    private String userId;
    private Long dataUsage;
    private Long callUsage;
    private Long messageUsage;
    private LocalDateTime timestamp;
}
