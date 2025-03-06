package com.himedia.spserver.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "point_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PointHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "memberid", nullable = false)
    private Member member; // 포인트 소유자

    @Column(name = "points", columnDefinition = "DECIMAL(10,2)", nullable = false)
    private BigDecimal points; // 적립/사용한 포인트

    @Enumerated(EnumType.STRING)
    @Column(name = "type", length = 10, nullable = false)
    private PointType type; // 적립(earn), 사용(use)

    public enum PointType {
        EARN, // 포인트 적립
        USE   // 포인트 사용
    }

    @Column(name = "description", length = 255, nullable = false)
    private String description; // 포인트 사용/적립 내용

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now(); // 내역 생성 날짜
}
