package com.telecom.cqrs.query;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.retry.annotation.EnableRetry;

/**
 * CQRS 패턴 데모 애플리케이션의 메인 클래스입니다.
 */
@SpringBootApplication
@EnableRetry
@ComponentScan(basePackages = {"com.telecom.cqrs.query", "com.telecom.cqrs.common"})
public class CqrsQueryApplication {
    public static void main(String[] args) {
        SpringApplication.run(CqrsQueryApplication.class, args);
    }
}
