package com.himedia.spserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "orders_detail")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrdersDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orders_detail_seq")
    private int ordersDetailSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_order_seq", referencedColumnName = "orderseq", nullable = false)
    @ToString.Exclude  // 무한 루프 방지
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    Orders orders;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "orders_product_seq", referencedColumnName = "productseq", nullable = false)
    @ToString.Exclude  // 무한 루프 방지
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_size_id", referencedColumnName = "id")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ProductOption productOption; // 선택한 상품 옵션 정보

    @Column(name = "size_value", length = 255) // ✅ 옵션(사이즈) 컬럼 추가
    private String sizeValue;

    @Column(name = "orders_quantity", nullable = false)
    int quantity;

    @Enumerated(EnumType.STRING)
    @Column(name = "orders_result", length = 20, nullable = false)
    @Builder.Default // ✅ 빌더 사용 시 기본값 설정
    OrderResult ordersResult = OrderResult.PENDING; // 기본값 설정

    public enum OrderResult {
        PENDING, CONFIRMED, SHIPPED, CANCELED, RETURNED, REFUNDED,DELIVERED,PURCHASE_CONFIRMED
    }

    @Column(name = "price", precision = 10, scale = 2)
    private BigDecimal price;
}
