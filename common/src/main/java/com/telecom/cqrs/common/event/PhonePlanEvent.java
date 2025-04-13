package com.telecom.cqrs.common.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
/**
 * 요금제 변경 이벤트를 표현하는 클래스입니다.
 * Event Bus를 통해 전달됩니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PhonePlanEvent {
    private String eventId;
    private String eventType;
    private String userId;
    private String planName;
    private int dataAllowance;
    private int callMinutes;
    private int messageCount;
    private double monthlyFee;
    private String status;
    private LocalDateTime timestamp;
}
