package com.himedia.spserver.service;

import com.himedia.spserver.dao.ProductDao;
import com.himedia.spserver.dto.ProductDTO;
import com.himedia.spserver.entity.Product;
import com.himedia.spserver.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional

public class ProductService {


    @Autowired
    private ProductRepository pr;

    @Autowired
    ProductDao pdao;

    public List<Product> getBestProduct() {
        // List<Product> list = pr.getBestProduct("Y"); // repository 를 이용한 방식
        // List<Product> list = pdao.getBestProduct(); // dao 를 이용한 방식
        List<Product> list = pr.findTop8ByProductBestOrderByIndateDesc("Y"); // findby 를 이용한 방식
        return list;
    }

    public List<Product> getNewProduct() {
        // List<Product> list = pr.getNewProduct(); // repository 를 이용한 방식
        // List<Product> list = pdao.getNewProduct(); // dao 를 이용한 방식
        List<Product> list = pr.findTop8ByOrderByIndateDesc(); // // findby 를 이용한 방식
        return list;
    }

    public List getProductImages(int productSeq) {
        // List<Product> list = pr.getProductImages(); // repository 를 이용한 방식
        // List<Product> list = pdao.getProductImages(); // dao 를 이용한 방식
        List<Product> list = pr.findByProductSeq(productSeq);  // findby 를 이용한 방식
        return list;
    }


    public List<Product> getProduct(int productSeq) {
        return pr.findByProductSeq(productSeq);
    }












}