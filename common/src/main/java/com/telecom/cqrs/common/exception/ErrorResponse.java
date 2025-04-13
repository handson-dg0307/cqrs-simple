package com.telecom.cqrs.common.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * API 오류 응답을 위한 DTO 클래스입니다.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "에러 응답")
public class ErrorResponse {
    @Schema(description = "에러 코드", example = "NOT_FOUND")
    private String code;

    @Schema(description = "에러 메시지", example = "요청한 리소스를 찾을 수 없습니다.")
    private String message;
}
