package com.himedia.spserver.repository;

import com.himedia.spserver.entity.Member;
import com.himedia.spserver.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Integer> {

    List<Review> findByProductProductSeq(int productSeq);

    @Query(value = "SELECT COUNT(*) FROM review WHERE memberid = :memberId", nativeQuery = true)
    Integer countReviewsByMemberId(@Param("memberId") String memberId);

    @Query("SELECT COUNT(r) FROM Review r WHERE r.product.productSeq = :productSeq")
    Long countReviewsByProductSeq(@Param("productSeq") int productSeq);

    @Modifying
    @Transactional
    @Query("DELETE FROM Review r WHERE r.member.memberId = :memberId")
    void deleteByMemberId(@Param("memberId") String memberId);

    // ✅ 수정된 부분: ordersDetail.orders.orderSeq → orderSeq 직접 사용
    @Query("SELECT r FROM Review r WHERE r.product.productSeq = :productSeq AND r.orderSeq = :orderSeq")
    List<Review> findByOrderSeqAndProductSeq(@Param("orderSeq") Integer orderSeq, @Param("productSeq") Integer productSeq);

    @Query("SELECT r FROM Review r WHERE r.product.productSeq = :productSeq AND r.member.memberId = :memberId")
    List<Review> findByMemberAndProduct(@Param("memberId") String memberId, @Param("productSeq") Integer productSeq);


    Page<Review> findByMemberMemberId(String memberId, Pageable pageable);

    boolean existsByOrderSeqAndProduct_ProductSeq(Integer orderSeq, Integer productSeq);
}