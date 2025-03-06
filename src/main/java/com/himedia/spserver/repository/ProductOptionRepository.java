package com.himedia.spserver.repository;

import com.himedia.spserver.entity.ProductOption;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductOptionRepository extends JpaRepository<ProductOption, Integer> {

    Optional<ProductOption> findBySizeValue(String sizeValue);
}
