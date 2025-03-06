package com.himedia.spserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.springframework.format.annotation.DateTimeFormat;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "review")
@Data
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reviewseq")
    private int reviewSeq;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE) // ✅ 회원 삭제 시 리뷰도 삭제
    @JoinColumn(name = "memberid", foreignKey = @ForeignKey(name = "FK_review_member", value = ConstraintMode.CONSTRAINT))
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Member member;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productseq")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Product product;

    // ✅ 주문번호 직접 추가 (기존 `ordersDetail`도 유지 가능)
    @Column(name = "orderseq", nullable = false)
    private Integer orderSeq;

    // ✅ 주문 정보 추가 (OrdersDetail과 연결)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_detail_seq", foreignKey = @ForeignKey(name = "FK_review_orders_detail"))
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private OrdersDetail ordersDetail;

    @Column(name = "reviewcontent", length = 255)
    private String reviewContent;

    @Column(name = "reviewrating", nullable = false)
    private int reviewRating;

    @Column(name = "reviewimage", length = 255, nullable = true)
    private String reviewImage;

    @Column(name = "reviewimage1", length = 255, nullable = true)
    private String reviewImage1;

    @Column(name = "reviewimage2", length = 255, nullable = true)
    private String reviewImage2;

    @Column(name = "indate", nullable = false, columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime indate = LocalDateTime.now();


}
