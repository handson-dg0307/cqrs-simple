package com.telecom.cqrs.common.constant;

public class EventHubConstants {
    // 이벤트 타입 상수
    public static final String EVENT_TYPE_PLAN = "PLAN_CHANGED";
    public static final String EVENT_TYPE_USAGE = "USAGE_UPDATED";
    
    // 토픽 이름 상수
    public static final String TOPIC_PLAN = "plan";
    public static final String TOPIC_USAGE = "usage";
    
    // 속성 키 상수
    public static final String PROPERTY_TYPE = "type";
    public static final String PROPERTY_TOPIC = "topic";

    private EventHubConstants() {}
}
