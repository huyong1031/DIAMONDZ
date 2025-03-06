package com.himedia.spserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_option_mapping",
        uniqueConstraints = @UniqueConstraint(columnNames = {"productseq", "option_size_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOptionMapping {
    // ProductOptionMapping을 통해 상품과 옵션(사이즈)를 연결하는 중간 테이블
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id; // 매핑 테이블의 고유 ID

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productseq", referencedColumnName = "productseq", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Product product; // Product 엔티티 참조(특정 상품)

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "option_size_id", referencedColumnName = "id", nullable = false)
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private ProductOption productOption; // 손자 카테고리, 특정 옵션(sizeValue)

    // ✅ Product와 OptionSize를 받는 생성자 추가
    public ProductOptionMapping(Product product, ProductOption productOption) {
        this.product = product;
        this.productOption = productOption;
    }

}
