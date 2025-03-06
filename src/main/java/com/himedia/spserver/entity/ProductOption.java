package com.himedia.spserver.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_option")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductOption {
    // 옵션(사이즈) 자체를 저장하는 엔티티
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "size_value", nullable = false, unique = true) // ✅ 필드명 변경: name → size_value
    private String sizeValue; // 반지: 10호, 11호, 12호 /  목걸이: 40cm, 41cm, 42cm / 귀걸이: 골드, 실버, 로즈골드 / 팔찌: 17cm, 18cm, 19cm
}
