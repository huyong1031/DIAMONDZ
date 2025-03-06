package com.himedia.spserver.repository;

import com.himedia.spserver.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminProductRepository extends JpaRepository<Product, Integer> {
    Page<Product> findByProductNameContaining(String key, Pageable pageable);
}