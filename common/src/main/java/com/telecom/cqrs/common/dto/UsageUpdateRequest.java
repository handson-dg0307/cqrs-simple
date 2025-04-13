package com.telecom.cqrs.common.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * 사용량 업데이트 요청을 위한 DTO 클래스입니다.
 */
@Data
@Schema(description = "사용량 업데이트 요청")
public class UsageUpdateRequest {
    @Schema(description = "사용자 ID", example = "user123", required = true)
    private String userId;

    @Schema(description = "데이터 사용량(GB)", example = "35")
    private Long dataUsage;

    @Schema(description = "통화 사용량(분)", example = "120")
    private Long callUsage;

    @Schema(description = "문자 사용량(건)", example = "50")
    private Long messageUsage;
}
