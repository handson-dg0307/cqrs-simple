package com.telecom.cqrs.common.exception;

/**
 * 요금제를 찾을 수 없을 때 발생하는 예외입니다.
 */
public class PhonePlanNotFoundException extends RuntimeException {
    public PhonePlanNotFoundException(String message) {
        super(message);
    }
}
