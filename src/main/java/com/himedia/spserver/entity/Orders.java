package com.himedia.spserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})  // ✅ Hibernate Proxy 문제 해결
public class Orders {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "orderseq")
    private Integer orderSeq;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberid")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productseq")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Product product;

    // ✅ ProductOption과의 연관관계 추가(sizeValue는 ProductOption에서 가져옴)
    // Orders 테이블의 option_size 컬럼은 ProductOption 테이블의 id(PK)를 외래 키(FK)로 참조
    // productOption 필드는 ProductOption 엔티티와 연결됩니다.
    // getSizeValue() 메서드를 통해 ProductOption의 sizeValue 값을 가져올 수 있음.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_size")  // 실제 DB 컬럼명
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ProductOption productOption;

    // ✅ sizeValue 값 직접 가져오는 getter 추가 (DTO에서 활용 가능)
    public String getSizeValue() {
        return this.productOption != null ? this.productOption.getSizeValue() : null;
    }

//    @Column(name = "productsize", length = 255)
//    private String productSize;

//    @Column(name = "sizevalue", length = 255)
//    private String sizeValue; 위의 option_size 와 중복

    @Column(name = "quantity", length = 255)
    private Integer quantity;

    @Column(name = "totalprice", length = 255)
    private BigDecimal totalPrice;

    @Column(name = "shippingaddress", length = 500)
    private String shippingAddress;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrderStatus orderStatus = OrderStatus.PENDING; // 주문 상태

    @CreationTimestamp
    @Column(name = "orderdate", length = 255)
    private LocalDateTime orderDate; // 주문 날짜

    @Column(name = "phone", length = 20)
    private String phone; // ✅ 전화번호 추가

    @Column(name = "delivery_date")
    private LocalDateTime deliveryDate; // ✅ 배송완료 날짜




}
