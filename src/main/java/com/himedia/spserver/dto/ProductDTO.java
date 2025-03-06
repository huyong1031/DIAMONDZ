package com.himedia.spserver.dto;

import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import com.himedia.spserver.entity.Categories;
import com.himedia.spserver.entity.Product;

@Getter
@Setter
public class ProductDTO {
    private Integer productSeq;
    private String productBest;
    private String productUse;
    private String productContent;
    private String productImage;
    private String productImage2;
    private String productImage3;
    private String productImage4;
    private String infoImage;
    private String infoImage2;
    private String infoImage3;
    private String infoImage4;
    private String infoImage5;
    private String productName;
    private Integer productCostPrice;
    private Integer productSalePrice;
    private Integer productMarginPrice;
    private LocalDateTime indate;
    private String hoverImage;
    private Integer categoryId;
    private String categoryName; // ✅ 부모 카테고리 이름 추가
    private Integer subCategoryId; // 세부 카테고리 ID 추가
    private String subCategoryName; // ✅ 자식 카테고리 이름 추가
    private List<String> categories; // 카테고리 이름 목록
    private String productStatus;
    private List<String> option;
    private Boolean isLiked; // ✅ 추가된 필드: 사용자가 좋아요한 상품인지 여부(기본값: false)

    // ✅ 추가된 필드: 대표 세부 카테고리
    private String subCategory;

    // 기본 생성자
    public ProductDTO() {}

    // 모든 필드를 초기화하는 생성자 (필요한 경우)
    public ProductDTO(Integer productSeq, String productBest, String productUse, String productContent,
                      String productImage, String productImage2, String productImage3, String productImage4,
                      String infoImage, String infoImage2, String infoImage3, String infoImage4, String infoImage5,
                      String productName, Integer productCostPrice, Integer productSalePrice, Integer productMarginPrice,
                      LocalDateTime indate, String hoverImage, Integer categoryId, Integer subCategoryId, List<String> categories,
                      String productStatus, String subCategory) {
        this.productSeq = productSeq;
        this.productBest = productBest;
        this.productUse = productUse;
        this.productContent = productContent;
        this.productImage = productImage;
        this.productImage2 = productImage2;
        this.productImage3 = productImage3;
        this.productImage4 = productImage4;
        this.infoImage = infoImage;
        this.infoImage2 = infoImage2;
        this.infoImage3 = infoImage3;
        this.infoImage4 = infoImage4;
        this.infoImage5 = infoImage5;
        this.productName = productName;
        this.productCostPrice = productCostPrice;
        this.productSalePrice = productSalePrice;
        this.productMarginPrice = productMarginPrice;
        this.indate = indate;
        this.hoverImage = hoverImage;
        this.categoryId = categoryId;
        this.subCategoryId = subCategoryId; //  세부 카테고리 ID 저장
        this.categories = categories;
        this.productStatus = productStatus;
        this.subCategory = subCategory;

    }

    // Product 엔티티를 ProductDTO로 변환하는 정적 팩토리 메서드
    public static ProductDTO fromEntity(Product product) {
        ProductDTO dto = new ProductDTO();
        dto.setProductSeq(product.getProductSeq());
        dto.setProductBest(product.getProductBest());
        dto.setProductUse(product.getProductUse());
        dto.setProductContent(product.getProductContent());
        dto.setProductImage(product.getProductImage());
        dto.setProductImage2(product.getProductImage2());
        dto.setProductImage3(product.getProductImage3());
        dto.setProductImage4(product.getProductImage4());
        dto.setInfoImage(product.getInfoImage());
        dto.setInfoImage2(product.getInfoImage2());
        dto.setInfoImage3(product.getInfoImage3());
        dto.setInfoImage4(product.getInfoImage4());
        dto.setInfoImage5(product.getInfoImage5());
        dto.setProductName(product.getProductName());
        dto.setProductCostPrice(product.getProductCostPrice());
        dto.setProductSalePrice(product.getProductSalePrice());
        dto.setProductMarginPrice(product.getProductMarginPrice());
        dto.setIndate(product.getIndate());
        dto.setHoverImage(product.getHoverImage());
        dto.setCategoryId(product.getCategoryId());
        dto.setProductStatus(product.getProductStatus());
        dto.setIsLiked(false);

        // ✅ Product 엔티티의 Categories 리스트를 DTO의 String 리스트로 변환
        if (product.getCategories() != null) {
            List<String> categoryNames = product.getCategories().stream()
                    .map(Categories::getCategoryName)
                    .collect(Collectors.toList());
            dto.setCategories(categoryNames);

            // ✅ 부모 카테고리 찾기
            Categories parentCategory = product.getCategories().stream()
                    .filter(c -> c.getParentCategory() == null) // 부모 카테고리
                    .findFirst()
                    .orElse(null);

            if (parentCategory != null) {
                dto.setCategoryId(parentCategory.getCategoryId());
                dto.setCategoryName(parentCategory.getCategoryName());
            }

            // ✅ 자식 카테고리 찾기
            Categories subCategory = product.getCategories().stream()
                    .filter(c -> c.getParentCategory() != null) // 자식 카테고리
                    .findFirst()
                    .orElse(null);

            if (subCategory != null) {
                dto.setSubCategoryId(subCategory.getCategoryId());
                dto.setSubCategoryName(subCategory.getCategoryName());
            }
        }
        return dto;
    }

    // ✅ 좋아요 상태를 설정하는 별도 메서드 추가
    public void setLikeStatus(Boolean isLiked) {
        this.isLiked = isLiked;
    }


}
