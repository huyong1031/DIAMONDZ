package com.himedia.spserver.controller;

import com.himedia.spserver.dto.ProductDTO;
import com.himedia.spserver.entity.Product;


import com.himedia.spserver.repository.CategoriesRepository;
import com.himedia.spserver.repository.ProductLikeRepository;
import com.himedia.spserver.repository.ProductRepository;

import com.himedia.spserver.service.CategoriesService;
import com.himedia.spserver.service.ProductService;
import com.himedia.spserver.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/product")
public class ProductController {

    @Autowired
    ProductService ps;

    @Autowired
    ReviewService rvs;

    @Autowired
    CategoriesService categoriesService;

    @Autowired
    ProductLikeRepository productLikeRepository;

    @Autowired
    ProductRepository productRepository;

    @GetMapping("/test")
    public  String test(){
        return "<h1>@@@Test Success@@@</h1>";
    }

    @GetMapping("/bestPro")
    public HashMap<String,Object> getBestPro(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("bestProduct", ps.getBestProduct());

        return result;
    }

    @GetMapping("/newPro")
    public HashMap<String,Object> getNewPro(){
        HashMap<String,Object> result = new HashMap<>();
        result.put("newProduct",ps.getNewProduct());

        return result;
    }

    @GetMapping("/selectPro")
    public HashMap<String,Object> getSelectPro(@RequestParam("productSeq") int productSeq) {
        HashMap<String,Object> result = new HashMap<>();
        List<Product> productImages = ps.getProductImages(productSeq); // 이미지 리스트 받아오기
        result.put("productImages", productImages); // productImages를 결과에 넣기
        return result;
    }

    @GetMapping("/getProduct")
    public HashMap<String, Object> getProduct(@RequestParam("productSeq") int productSeq) {
        System.out.println("🟢 요청받은 productSeq: " + productSeq);
        HashMap<String, Object> result = new HashMap<>();

        List<Product> products = ps.getProduct(productSeq);

        if (products.isEmpty()) {
            System.out.println("🔴 해당 productSeq의 상품을 찾을 수 없음.");
            result.put("product", null);
        } else {
            ProductDTO productDTO = ProductDTO.fromEntity(products.get(0)); // DTO 변환 추가
            System.out.println("🟢 변환된 ProductDTO: " + productDTO);
            result.put("product", productDTO);
        }
        return result;
    }


    @Autowired
    ProductRepository pR;
    @Autowired
    CategoriesRepository categoryR;

    @GetMapping("/categoryList")
    public ResponseEntity<List<ProductDTO>> getProductsByCategory(
            @RequestParam Integer categoryId, // ✅ categoryId가 부모 카테고리 ID 역할을 함
            @RequestParam(required = false) String subCategory) {
        List<Product> products;

        if (subCategory == null || subCategory.equals("전체")) {
            products = pR.findByCategoryId(categoryId);
        } else {
            // ✅ 부모 카테고리 ID와 함께 세부 카테고리 ID 조회
            Integer subCategoryId = categoriesService.getSubCategoryIdByName(subCategory, categoryId); // ✅ parentCategoryId 전달

            if (subCategoryId != -1) {
                products = pR.findByCategoryAndSubCategory(categoryId, subCategory);
            } else {
                products = List.of();
            }
        }
        List<ProductDTO> productDTOs = products.stream()
                .map(ProductDTO::fromEntity)
                .collect(Collectors.toList());

        return ResponseEntity.ok(productDTOs);
    }

    // 상품 검색기능
    @GetMapping("/search")
    public ResponseEntity<List<ProductDTO>> searchProducts(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String memberId,
            @RequestParam(required = false) String minPrice,
            @RequestParam(required = false) String maxPrice
    ) {
        // ✅ 백엔드에서 받은 원래의 String 값 확인!!!!
        System.out.println("🔍 [DEBUG] 백엔드에서 받은 원래의 최소 가격값 (String)333: " + minPrice);
        System.out.println("🔍 [DEBUG] 백엔드에서 받은 원래의 최대 가격값 (String)333: " + maxPrice);

        // ✅ String 값을 Integer로 변환 (빈 값일 경우 null 처리)
        Integer minPriceValue = (minPrice != null && !minPrice.isEmpty()) ? Integer.parseInt(minPrice) : null;
        Integer maxPriceValue = (maxPrice != null && !maxPrice.isEmpty()) ? Integer.parseInt(maxPrice) : null;

        // ✅ 변환된 Integer 값 확인
        System.out.println("💰 [DEBUG] 변환된 최소 가격: " + minPriceValue + " | 최대 가격: " + maxPriceValue);
        // ✅ 검색어가 없는 경우 전체 제품 검색 가능하도록 변경
        List<Product> products = (keyword != null && !keyword.isEmpty())
                ? productRepository.searchByProductName(keyword)
                : productRepository.findAll();

        // ✅ 가격 필터링 적용 => 필터링 기준을 `productSalePrice`로 변경
        if (minPriceValue != null) {
            System.out.println("🔍 [FILTER] 최소 가격 적용: " + minPriceValue);
            products = products.stream()
                    .filter(product -> product.getProductSalePrice() >= minPriceValue)
                    .collect(Collectors.toList());
        }

        if (maxPriceValue != null) {
            System.out.println("🔍 [FILTER] 최대 가격 적용: " + maxPriceValue);
            products = products.stream()
                    .filter(product -> product.getProductSalePrice() <= maxPriceValue)
                    .collect(Collectors.toList());
        }

        if (products.isEmpty()) {
            System.out.println("🚨 [DEBUG] 필터링 결과: 검색된 제품이 없음");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(List.of());
        }

        List<Integer> likedProductIds = memberId != null
                ? productLikeRepository.findLikedProductIdsByMemberId(memberId)
                : List.of();

        System.out.println("❤️ [DEBUG] 좋아요한 상품 목록: " + likedProductIds);
        // ✅ 검색된 상품 목록을 DTO로 변환하면서 좋아요 여부 추가
        List<ProductDTO> productDTOs = products.stream()
                .map(product -> {
                    ProductDTO dto = ProductDTO.fromEntity(product);
                    boolean isLiked = likedProductIds.contains(product.getProductSeq());
                    dto.setLikeStatus(isLiked);
                    System.out.println("🛍 [DEBUG] 검색된 상품: " + product.getProductName() + " | 좋아요 상태: " + isLiked);
                    return dto;
                })
                .collect(Collectors.toList());

        return ResponseEntity.ok(productDTOs);
    }

    @GetMapping("/{productSeq}")
    public ResponseEntity<?> getProductById(@PathVariable int productSeq) {
        List<Product> products = productRepository.findByProductSeq(productSeq); // ✅ List<Product> 반환

        if (!products.isEmpty()) {
            return ResponseEntity.ok(products.get(0)); // ✅ 첫 번째 상품만 반환
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("상품을 찾을 수 없습니다.");
        }
    }




}
