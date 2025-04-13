package com.telecom.cqrs.common.exception;

public class PhonePlanChangeException extends RuntimeException {
    public PhonePlanChangeException(String message) {
        super(message);
    }

    public PhonePlanChangeException(String message, Throwable cause) {
        super(message, cause);
    }
}
