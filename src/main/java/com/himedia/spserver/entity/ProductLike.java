package com.himedia.spserver.entity;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;

@Entity
@Table(name = "product_like")
@Data
@ToString(exclude = "member") // ✅ 무한 루프 방지
public class ProductLike {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "likeseq")
    private int likeSeq;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memberid")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "productseq")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Product product;


}
