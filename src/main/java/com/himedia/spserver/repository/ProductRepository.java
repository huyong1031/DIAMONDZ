package com.himedia.spserver.repository;


import com.himedia.spserver.entity.Member;
import com.himedia.spserver.entity.Product;
import com.himedia.spserver.entity.ProductLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer> {

    // ✅ 네이티브 쿼리 수정 (bestyn → productBest)
    @Query(value="SELECT * FROM product WHERE productbest=:bestyn ORDER BY indate DESC LIMIT 8", nativeQuery=true)
    List<Product> getBestProduct(@Param("bestyn") String bestyn);

    @Query(value="SELECT * FROM product ORDER BY indate DESC LIMIT 4", nativeQuery=true)
    List<Product> getNewProduct();

    // ✅ 필드명 수정
    List<Product> findTop8ByProductBestOrderByIndateDesc(String y);

    List<Product> findTop8ByOrderByIndateDesc();

    List<Product> findByProductSeq(int productSeq);

    @Query("SELECT p FROM Product p WHERE p.categoryId = :categoryId")
    List<Product> findByCategoryId(@Param("categoryId") int categoryId);

    // 특정 카테고리 내의 상품 목록을 조회하면서 세부 카테고리 정보 포함
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.categoryName = :subCategory")
    List<Product> findBySubCategory(String subCategory);

//    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.parentCategory.categoryId = :categoryId AND c.categoryName = :subCategory")
//    List<Product> findByCategoryAndSubCategory(@Param("categoryId") int categoryId, @Param("subCategory") String subCategory);
    @Query("SELECT p FROM Product p JOIN p.categories c WHERE c.parentCategory.categoryId = :categoryId AND c.categoryId IN " +
            "(SELECT sub.categoryId FROM Categories sub WHERE sub.categoryName = :subCategory)")
    List<Product> findByCategoryAndSubCategory(@Param("categoryId") int categoryId, @Param("subCategory") String subCategory);
    
    // 상품(제품) 검색기능
    @Query("SELECT p FROM Product p WHERE LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    List<Product> searchByProductName(@Param("keyword") String keyword);


//    @Query("select p from Product p where p.productSeq=:productSeq")
//    Product findByProductSeq(int productSeq);



}
