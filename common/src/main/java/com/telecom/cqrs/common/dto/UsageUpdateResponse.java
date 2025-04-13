package com.telecom.cqrs.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 사용량 업데이트 응답을 위한 DTO 클래스입니다.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "사용량 업데이트 응답")
public class UsageUpdateResponse {
    @Schema(description = "성공 여부", example = "true")
    private boolean success;

    @Schema(description = "응답 메시지", example = "사용량이 성공적으로 업데이트되었습니다.")
    private String message;

    @Schema(description = "사용자 ID", example = "user123")
    private String userId;
}
