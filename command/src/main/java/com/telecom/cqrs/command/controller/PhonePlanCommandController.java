package com.telecom.cqrs.command.controller;

import com.telecom.cqrs.command.domain.PhonePlan;
import com.telecom.cqrs.common.dto.UsageUpdateRequest;
import com.telecom.cqrs.common.dto.UsageUpdateResponse;
import com.telecom.cqrs.command.service.PhonePlanCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 요금제 변경과 사용량 업데이트 API를 제공하는 컨트롤러입니다.
 */
@RestController
@RequestMapping("/api/plans")
@RequiredArgsConstructor
@Tag(name = "요금제 관리 API", description = "요금제 변경과 사용량 업데이트 관련 API를 제공합니다.")
public class PhonePlanCommandController {
    private final PhonePlanCommandService phonePlanCommandService;

    /**
     * 요금제를 변경합니다.
     *
     * @param phonePlan 변경할 요금제 정보
     * @return 변경된 요금제 정보
     */
    @Operation(summary = "요금제 변경", description = "사용자의 요금제를 변경합니다.")
    @PostMapping("/command/change")
    public ResponseEntity<PhonePlan> changePhonePlan(
            @RequestBody PhonePlan phonePlan
    ) {
        return ResponseEntity.ok(phonePlanCommandService.changePhonePlan(phonePlan));
    }

    /**
     * 사용자의 사용량을 업데이트합니다.
     *
     * @param request 사용량 업데이트 요청
     * @return 업데이트 결과
     */
    @Operation(summary = "사용량 업데이트",
            description = "사용자의 데이터, 통화, 문자 사용량을 업데이트합니다.")
    @PostMapping("/command/usage")
    public ResponseEntity<UsageUpdateResponse> updateUsage(
            @RequestBody UsageUpdateRequest request
    ) {
        UsageUpdateResponse response = phonePlanCommandService.updateUsage(request);
        return ResponseEntity.ok(response);
    }
}
