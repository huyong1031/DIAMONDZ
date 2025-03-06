package com.himedia.spserver.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
@Data
@DynamicUpdate
public class Product implements ApplicationEventPublisherAware {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "productseq")
    private Integer productSeq;

    @Column(name = "productbest", length = 255)
    private String productBest;

    @Column(name = "productuse", length = 255)
    private String productUse;

    @Column(name = "productcontent", length = 255)
    private String productContent;

    @Column(name = "productimage", nullable = false, length = 255)
    private String productImage;

    @Column(name = "productimage2", length = 255)
    private String productImage2;

    @Column(name = "productimage3", length = 255)
    private String productImage3;

    @Column(name = "productimage4", length = 255)
    private String productImage4;

    @Column(name = "infoimage", length = 255)
    private String infoImage;

    @Column(name = "infoimage2", length = 255)
    private String infoImage2;

    @Column(name = "infoimage3", length = 255)
    private String infoImage3;

    @Column(name = "infoimage4", length = 255)
    private String infoImage4;

    @Column(name = "infoimage5", length = 255)
    private String infoImage5;

    @Column(name = "productname", length = 255)
    private String productName;

    @Column(name = "productcostprice", nullable = false)
    private int productCostPrice;

    @Column(name = "productsaleprice", nullable = false)
    private int productSalePrice;

    @Column(name = "productmarginprice", nullable = false)
    private int productMarginPrice;

    @Column(name = "indate", nullable = false)
    private LocalDateTime indate;

    @Column(name = "hoverimage", length = 255)
    private String hoverImage;


    @Column(name = "categoryid", nullable = false)
    private int categoryId; // 부모 카테고리 ID (예: 목걸이)

    @Column(name = "subcategoryid")
    private Integer subCategoryId; //  세부 카테고리 ID 추가 (예: 일체형, 메달형 등)

//    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
//    private List<ProductSize> sizes = new ArrayList<>();

    @Column(name = "categories_string", length = 255) // 새로운 String 타입 필드 추가
    private String categoriesString;

    // N:M 관계를 위한 다대다 매핑
    @ManyToMany
    @JoinTable(
            name = "product_category_mapping", // 중간 테이블 이름
            joinColumns = @JoinColumn(name = "productseq"), // Product 테이블의 외래키
            inverseJoinColumns = @JoinColumn(name = "category_id") // Categories 테이블의 외래키
    )
    @JsonIgnoreProperties({"parentCategory", "subCategories", "hibernateLazyInitializer", "handler"}) // 이거였네...(훈식)
    private List<Categories> categories = new ArrayList<>();

    @Column(name = "productstatus", length = 255)
    private String productStatus;

    @Transient
    private ApplicationEventPublisher eventPublisher;

    @Override
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.eventPublisher = applicationEventPublisher;
    }

    @PostPersist
    @PostUpdate
    public void triggerOptionSizeMapping() {
        if (eventPublisher != null) {
            eventPublisher.publishEvent(this);
        }
    }


}