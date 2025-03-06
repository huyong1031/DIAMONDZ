package com.himedia.spserver.repository;

import com.himedia.spserver.entity.Cart;
import com.himedia.spserver.entity.Member;
import com.himedia.spserver.entity.ProductOptionMapping;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findByMemberMemberId(String memberId);
    Optional<Cart> findByMemberAndProductOption(Member member, ProductOptionMapping productOption);

    Optional<Cart> findByCartSeq(int cartSeq);

    // ✅ [수정됨] 특정 회원의 특정 상품을 개별 삭제하는 메서드 (IN 제거)
    @Modifying
    @Transactional
    @Query("DELETE FROM Cart c WHERE c.member.memberId = :memberId AND c.product.productSeq = :productSeq")
    void deleteByMemberIdAndProductSeq(@Param("memberId") String memberId, @Param("productSeq") Integer productSeq);

}
    //Cart findByCartSeq(int cartSeq);




