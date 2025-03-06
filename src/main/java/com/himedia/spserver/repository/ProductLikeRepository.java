package com.himedia.spserver.repository;

import com.himedia.spserver.entity.ProductLike;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
    // 수정된 부분: productSeq 대신 Product 객체로 조회
    ProductLike findByMemberMemberIdAndProductProductSeq(String memberId, int productSeq);

    // memberId로 좋아요 목록을 조회
    List<ProductLike> findByMember_MemberId(String memberId);

    // memberId와 productSeq로 좋아요 목록을 조회
    List<ProductLike> findByMember_MemberIdAndProduct_ProductSeq(String memberId, Long productSeq);

    @Modifying
    @Transactional
    @Query("DELETE FROM ProductLike p WHERE p.member.memberId = :memberId")
    void deleteByMemberId(@Param("memberId") String memberId);

    @Query("SELECT pl.product.productSeq FROM ProductLike pl WHERE pl.member.memberId = :memberId")
    List<Integer> findLikedProductIdsByMemberId(@Param("memberId") String memberId);

}


