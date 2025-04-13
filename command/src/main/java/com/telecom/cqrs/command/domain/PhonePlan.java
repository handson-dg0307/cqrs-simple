package com.telecom.cqrs.command.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * 요금제 정보를 저장하는 엔티티 클래스입니다.
 * Write DB(PostgreSQL)에 저장됩니다.
 */
@Entity
@Table(name = "phone_plans",
        uniqueConstraints = @UniqueConstraint(columnNames = {"userId"}))
@Getter @Setter
@NoArgsConstructor
public class PhonePlan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String userId;

    private String planName;
    private int dataAllowance;
    private int callMinutes;
    private int messageCount;
    private double monthlyFee;
    private String status;
}
