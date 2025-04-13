package com.telecom.cqrs.command.config;

import com.telecom.cqrs.command.domain.PhonePlan;
import com.telecom.cqrs.command.service.PhonePlanCommandService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.Arrays;
import java.util.List;

/**
 * 샘플 요금제 데이터를 생성하는 설정 클래스입니다.
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class SampleDataLoader {

    /**
     * 애플리케이션 시작 시 샘플 데이터를 생성합니다.
     */
    @Bean
    @Profile("!prod") // 운영 환경이 아닐 때만 실행
    public CommandLineRunner initSampleData(PhonePlanCommandService commandService) {
        return args -> {
            log.info("Initializing sample phone plans...");

            List<PhonePlan> samplePlans = Arrays.asList(
                    createPhonePlan("user1", "Basic 5G", 10240, 200, 100, 45000),
                    createPhonePlan("user2", "Premium Unlimited", 999999, 999999, 999999, 85000),
                    createPhonePlan("user3", "Student Special", 20480, 100, 50, 35000),
                    createPhonePlan("user4", "Senior Care", 5120, 500, 200, 30000),
                    createPhonePlan("user5", "Data Only", 30720, 0, 0, 40000)
            );

            for (PhonePlan plan : samplePlans) {
                try {
                    commandService.changePhonePlan(plan);
                    log.info("Created sample plan: {}", plan.getPlanName());
                } catch (Exception e) {
                    log.error("Error creating sample plan {}: {}", plan.getPlanName(), e.getMessage());
                }
            }

            log.info("Sample data initialization completed");
        };
    }

    /**
     * PhonePlan 객체를 생성합니다.
     */
    private PhonePlan createPhonePlan(
            String userId,
            String planName,
            int dataAllowance,
            int callMinutes,
            int messageCount,
            double monthlyFee
    ) {
        PhonePlan plan = new PhonePlan();
        plan.setUserId(userId);
        plan.setPlanName(planName);
        plan.setDataAllowance(dataAllowance);
        plan.setCallMinutes(callMinutes);
        plan.setMessageCount(messageCount);
        plan.setMonthlyFee(monthlyFee);
        plan.setStatus("ACTIVE");
        return plan;
    }
}
