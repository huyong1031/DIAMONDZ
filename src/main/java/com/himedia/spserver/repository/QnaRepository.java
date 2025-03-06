package com.himedia.spserver.repository;

import com.himedia.spserver.entity.Qna;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Collection;

public interface QnaRepository extends JpaRepository<Qna, Integer> {
    Qna findByQnaSeqAndMember_MemberId(int qnaSeq, String memberId);

    // ✅ 로그인한 사용자의 QnA 목록 조회
    Page<Qna> findByMember_MemberId(String memberId, Pageable pageable);

    int countAllBy();

    Collection<Object> findBySubjectContaining(String key);

    Page<Qna> findAllBySubjectContaining(String key, Pageable pageable);

    Qna findByQnaSeq(int qnaSeq);
}
