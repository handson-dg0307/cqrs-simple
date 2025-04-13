package com.telecom.cqrs.command;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

/**
 * CQRS 패턴 데모 애플리케이션의 메인 클래스입니다.
 */
@SpringBootApplication
@ComponentScan(basePackages = {"com.telecom.cqrs.command", "com.telecom.cqrs.common"})
public class CqrsCommandApplication {
    public static void main(String[] args) {
        SpringApplication.run(CqrsCommandApplication.class, args);
    }
}
