package com.himedia.spserver.repository;

import com.himedia.spserver.entity.PointHistory;
import com.himedia.spserver.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PointHistoryRepository extends JpaRepository<PointHistory, Long> {
    Page<PointHistory> findByMember_MemberIdOrderByCreatedAtDesc(String memberId, Pageable pageable);

    @Query("SELECT COALESCE(SUM(p.points), 0) FROM PointHistory p WHERE p.member.memberId = :memberId")
    BigDecimal getTotalPoints(@Param("memberId") String memberId);

}

