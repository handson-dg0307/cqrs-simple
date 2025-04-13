package com.telecom.cqrs.common.exception;

/**
 * 사용량 업데이트 중 발생하는 예외를 처리하는 클래스입니다.
 */
public class UsageUpdateException extends RuntimeException {
    /**
     * 메시지와 함께 예외를 생성합니다.
     *
     * @param message 예외 메시지
     */
    public UsageUpdateException(String message) {
        super(message);
    }

    /**
     * 메시지와 원인 예외와 함께 예외를 생성합니다.
     *
     * @param message 예외 메시지
     * @param cause 원인 예외
     */
    public UsageUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}
