package com.himedia.spserver.repository;

import com.himedia.spserver.entity.Member;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MemberRepository extends JpaRepository<Member, String> {
    Optional<Member> findByMemberId(String memberId);
    boolean existsById(String memberId); // 아이디 중복 확인

    @EntityGraph(attributePaths = {"memberRoleList"})
    @Query("select m from Member m where m.memberId=:memberId")
    Member getWithRoles(@Param("memberId")String memberId);
}
