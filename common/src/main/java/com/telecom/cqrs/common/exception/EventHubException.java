package com.telecom.cqrs.common.exception;

/**
 * Event Hub 관련 예외를 처리하는 클래스입니다.
 */
public class EventHubException extends RuntimeException {
    public EventHubException(String message) {
        super(message);
    }

    public EventHubException(String message, Throwable cause) {
        super(message, cause);
    }
}
