package com.himedia.spserver.service;

import com.himedia.spserver.repository.CategoriesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CategoriesService {
    @Autowired
    private CategoriesRepository categoryR;

    // ✅ 부모 카테고리 ID까지 고려하여 세부 카테고리 ID 조회
    public Integer getSubCategoryIdByName(String subCategory, Integer parentCategoryId) {
        if (parentCategoryId == null) {
            System.out.println("⚠️ parentCategoryId가 NULL이므로 조회할 수 없음.");
            return -1;
        }

        Optional<Integer> subCategoryIdOpt = categoryR.findCategoryIdByName(subCategory, parentCategoryId);
        Integer subCategoryId = subCategoryIdOpt.orElse(-1);

        System.out.println("Service - Converted subCategory [" + subCategory + "] under parentCategory [" + parentCategoryId + "] to subCategoryId: " + subCategoryId);
        return subCategoryId;
    }

}
