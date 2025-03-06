package com.himedia.spserver.dao;

import com.himedia.spserver.entity.Product;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public class ProductDao {

    @Autowired
    EntityManager em;

    // ✅ 베스트 상품 조회 (JPQL 필드 수정)
    @Transactional
    public List<Product> getBestProduct() {
        String jpql = "SELECT p FROM Product p WHERE p.productBest = :bestyn ORDER BY p.indate DESC";
        return em.createQuery(jpql, Product.class)
                .setParameter("bestyn", "Y")
                .setFirstResult(0)
                .setMaxResults(4)
                .getResultList();
    }

    // ✅ 최신 상품 조회
    @Transactional
    public List<Product> getNewProduct() {
        String jpql = "SELECT p FROM Product p ORDER BY p.indate DESC";
        return em.createQuery(jpql, Product.class)
                .setFirstResult(0)
                .setMaxResults(4)
                .getResultList();
    }
}
