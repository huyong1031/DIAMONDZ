package com.himedia.spserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Entity
@Table(name = "cart")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cart {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cartseq")
    private int cartSeq;

    @Column(name = "quantity", nullable = false)
    private int quantity;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberid", referencedColumnName = "memberid", nullable = false)
    @ToString.Exclude  //  무한 루프 방지
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productseq", referencedColumnName = "productseq", nullable = false)
    @ToString.Exclude  //  무한 루프 방지
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_option_id", referencedColumnName = "id", nullable = false)
    @ToString.Exclude
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ProductOption productOption; // 선택한 손자 카테고리 옵션 (10호, 40cm 등)

    @CreationTimestamp
    @Column(name = "indate", nullable = false, updatable = false)
    private Timestamp indate;

    // ✅ productImage Getter 추가
    public String getProductImage() {
        return this.product != null ? this.product.getProductImage() : null;
    }

    // @ManyToOne (다대일 관계 설정) : Cart 엔터티는 Member 엔터티와 다대일(N:1) 관계.
    //여러 개의 Cart가 하나의 Member에 속할 수 있습니다.
    // @ManyToOne은 현재 엔터티(Cart)가 다른 엔터티(Member)를 참조하고 있음을 의미

    // @JoinColumn(name = "memberId") (외래 키 설정)
    // 현재 테이블(cart)의 member_userid 컬럼이 Member 엔터티의 기본 키(PK) 또는 유니크 키를 참조한다는 의미
}
