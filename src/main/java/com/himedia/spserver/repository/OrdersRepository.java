package com.himedia.spserver.repository;

import com.himedia.spserver.entity.Orders;
import com.himedia.spserver.entity.OrdersDetail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrdersRepository extends JpaRepository<Orders, Integer> {
    @Query("SELECT o FROM Orders o LEFT JOIN FETCH o.product WHERE o.member.memberId = :memberId")
    Page<Orders> findByMember_MemberIdWithProduct(@Param("memberId") String memberId, Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE FROM Orders o WHERE o.member.memberId = :memberId")
    void deleteByMemberId(@Param("memberId") String memberId);

    int countAllBy();

    int countByMember_MemberIdContaining(String memberId);

    int countByMember_MemberIdContainingOrOrderSeq(String memberId, int orderSeq);

    // 기존 메서드는 유지하되, 새로운 FETCH JOIN 메서드 추가
    @Query("SELECT o FROM Orders o LEFT JOIN FETCH o.product LEFT JOIN FETCH o.member WHERE o.member.memberId LIKE %:memberId% OR o.orderSeq = :orderSeq")
    Page<Orders> findByMember_MemberIdContainingOrOrderSeqWithDetails(@Param("memberId") String memberId, @Param("orderSeq") int orderSeq, Pageable pageable);

    @Query("SELECT o FROM Orders o LEFT JOIN FETCH o.product LEFT JOIN FETCH o.member WHERE o.orderSeq = :orderSeq")
    Optional<Orders> findByOrderSeqWithDetails(@Param("orderSeq") int orderSeq);

    @Query("SELECT o FROM Orders o LEFT JOIN FETCH o.product LEFT JOIN FETCH o.member")
    Page<Orders> findAllWithDetails(Pageable pageable);

    @Query("SELECT o FROM Orders o LEFT JOIN FETCH o.product LEFT JOIN FETCH o.member WHERE o.member.memberId LIKE %:memberId%")
    Page<Orders> findByMember_MemberIdContainingWithDetails(@Param("memberId") String memberId, Pageable pageable);

    @Query("SELECT d FROM OrdersDetail d WHERE d.orders.orderSeq = :orderSeq AND d.product.productSeq = :productSeq")
    List<OrdersDetail> findByOrderSeqAndProductSeq(@Param("orderSeq") Integer orderSeq, @Param("productSeq") Integer productSeq);

    @Query("SELECT d FROM OrdersDetail d WHERE d.orders.orderSeq = :orderSeq")
    List<OrdersDetail> findByOrders_OrderSeq(@Param("orderSeq") Integer orderSeq);


}
