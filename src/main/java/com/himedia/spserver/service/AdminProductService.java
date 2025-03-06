package com.himedia.spserver.service;

import com.himedia.spserver.dto.Paging;
import com.himedia.spserver.entity.Product;
import com.himedia.spserver.repository.AdminProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime; // 추가
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AdminProductService {

    @Autowired
    private AdminProductRepository adminProductRepository;

    public HashMap<String, Object> getProductList(int page, String key) {
        HashMap<String, Object> result = new HashMap<>();
        Pageable pageable = PageRequest.of(page - 1, 5, Sort.by("productSeq").descending());

        Page<Product> productPage;
        if (key == null || key.trim().isEmpty()) {
            productPage = adminProductRepository.findAll(pageable);
        } else {
            productPage = adminProductRepository.findByProductNameContaining(key, pageable);
        }

        List<Product> productList = productPage.getContent();
        int totalCount = (int) productPage.getTotalElements();

        Paging paging = new Paging();
        paging.setPage(page);
        paging.setTotalCount(totalCount);
        paging.setDisplayRow(5);
        paging.calPaging();

        result.put("productList", productList);
        result.put("paging", paging);
        result.put("key", key);

        return result;
    }

    public Product getProduct(Integer productSeq) {
        Optional<Product> product = adminProductRepository.findById(productSeq);
        return product.orElse(null);
    }

    public Product saveProduct(Product product) {
        if (product.getProductSeq() == null) { // productSeq가 null이면 INSERT (신규 등록)
            product.setIndate(LocalDateTime.now()); // 신규 등록 시에만 indate 설정
        }
        return adminProductRepository.save(product); // JpaRepository.save() 는 INSERT/UPDATE 자동 처리
    }

    public void deleteProduct(int intExact) {
        adminProductRepository.deleteById(intExact);
    }
}