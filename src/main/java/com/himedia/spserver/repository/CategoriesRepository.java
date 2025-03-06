package com.himedia.spserver.repository;

import com.himedia.spserver.entity.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface CategoriesRepository extends JpaRepository<Categories, Integer> {

    // ✅ 카테고리 이름으로 ID 조회하는 쿼리
    @Query("SELECT c.categoryId FROM Categories c WHERE c.categoryName = :categoryName AND c.parentCategory.categoryId = :parentCategoryId")
    Optional<Integer> findCategoryIdByName(@Param("categoryName") String categoryName, @Param("parentCategoryId") Integer parentCategoryId);

}
