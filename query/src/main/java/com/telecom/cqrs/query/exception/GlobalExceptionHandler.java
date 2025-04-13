package com.telecom.cqrs.query.exception;

import com.telecom.cqrs.common.exception.ErrorResponse;
import com.telecom.cqrs.common.exception.PhonePlanNotFoundException;
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

    @ExceptionHandler(PhonePlanNotFoundException.class)
    public ResponseEntity<ErrorResponse> handlePhonePlanNotFound(PhonePlanNotFoundException e) {
        log.error("Error occurred: ", e);  // 스택 트레이스와 함께 로그 출력
        ErrorResponse response = new ErrorResponse("NOT_FOUND", e.getMessage());
        return ResponseEntity.status(404).body(response);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericError(Exception e) {
        log.error("Unexpected error occurred: ", e);  // 스택 트레이스와 함께 로그 출력
        ErrorResponse response = new ErrorResponse("INTERNAL_SERVER_ERROR", "내부 서버 오류가 발생했습니다.");
        return ResponseEntity.status(500).body(response);
    }
}
