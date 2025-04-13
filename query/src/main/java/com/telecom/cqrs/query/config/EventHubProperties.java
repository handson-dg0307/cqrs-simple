package com.telecom.cqrs.query.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "eventhub")
@Getter @Setter
public class EventHubProperties {
    // 단일 EventHub 설정으로 변경
    private String hubName;
    private String consumerGroup = "$Default";
    private Integer batchSize = 100;
    private Long checkpointInterval = 5000L; // 5초
}
