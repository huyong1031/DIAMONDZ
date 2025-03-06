package com.himedia.spserver.repository;

import com.himedia.spserver.entity.Admin;
import com.himedia.spserver.entity.Member;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Member, String> { // Member 엔티티를 기본으로 사용

    // Admin 엔티티 관련 메서드
    @Query("SELECT a FROM Admin a WHERE a.adminId =:adminId") // Admin 엔티티에 대한 쿼리 추가
    Optional<Admin> findByAdminId(String adminId);

    // Member 엔티티 관련 메서드 (JPQL 쿼리 사용)
    @Query("SELECT m FROM Member m WHERE m.memberName LIKE %:key%")
    Page<Member> findByMemberNameContaining(String key, Pageable pageable);

    @Query("SELECT m FROM Member m WHERE m.memberId LIKE %:key%")
    Page<Member> findByMemberIdContaining(String key, Pageable pageable);
}