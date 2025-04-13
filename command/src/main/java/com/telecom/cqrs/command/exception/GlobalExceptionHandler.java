package com.telecom.cqrs.command.exception;

import com.telecom.cqrs.common.exception.ErrorResponse;
import com.telecom.cqrs.common.exception.EventHubException;
import com.telecom.cqrs.common.exception.PhonePlanNotFoundException;
import com.telecom.cqrs.common.exception.UsageUpdateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 전역 예외 처리를 담당하는 핸들러입니다.
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 요금제를 찾을 수 없는 경우의 예외를 처리합니다.
     */
    @ExceptionHandler(PhonePlanNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePhonePlanNotFound(PhonePlanNotFoundException e) {
        log.error("요금제를 찾을 수 없음: {}", e.getMessage(), e);
        ErrorResponse response = new ErrorResponse("NOT_FOUND", e.getMessage());
        return ResponseEntity.status(404).body(response);
    }

    /**
     * 사용량 업데이트 관련 예외를 처리합니다.
     */
    @ExceptionHandler(UsageUpdateException.class)
    public ResponseEntity<ErrorResponse> handleUsageUpdateError(UsageUpdateException e) {
        log.error("사용량 업데이트 오류: {}", e.getMessage(), e);
        ErrorResponse response = new ErrorResponse("USAGE_UPDATE_ERROR", e.getMessage());
        return ResponseEntity.status(400).body(response);
    }

    /**
     * Event Hub 관련 예외를 처리합니다.
     */
    @ExceptionHandler({EventHubException.class})
    public ResponseEntity<ErrorResponse> handleEventHubError(EventHubException e) {
        log.error("Event Hub 오류: {}", e.getMessage(), e);
        ErrorResponse response = new ErrorResponse("EVENT_HUB_ERROR", "이벤트 발행 중 오류가 발생했습니다");
        return ResponseEntity.status(500).body(response);
    }

    /**
     * 예상치 못한 예외를 처리합니다.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(Exception e) {
        log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
        ErrorResponse response = new ErrorResponse("INTERNAL_SERVER_ERROR", "내부 서버 오류가 발생했습니다");
        return ResponseEntity.status(500).body(response);
    }
}
