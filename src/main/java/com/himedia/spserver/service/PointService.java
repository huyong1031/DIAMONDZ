package com.himedia.spserver.service;

import com.himedia.spserver.entity.Member;
import com.himedia.spserver.entity.PointHistory;
import com.himedia.spserver.repository.MemberRepository;
import com.himedia.spserver.repository.PointHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class PointService {

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    // 포인트 적립
    @Transactional
    public void earnPoints(String memberId, BigDecimal amount, String description) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        // ✅ 현재 포인트 업데이트
        member.setMemberPoints(member.getMemberPoints().add(amount));
        memberRepository.save(member); // 🔥 반드시 저장

        // ✅ 포인트 내역 저장
        PointHistory history = new PointHistory();
        history.setMember(member);
        history.setPoints(amount);
        history.setType(PointHistory.PointType.EARN);
        history.setDescription(description);
        history.setCreatedAt(LocalDateTime.now());

        pointHistoryRepository.save(history);
    }



    // 포인트 사용
    @Transactional
    public void usePoints(String memberId, BigDecimal amount, String description) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        if (member.getMemberPoints().compareTo(amount) < 0) {
            throw new IllegalStateException("포인트 부족");
        }

        // ✅ 현재 포인트 차감
        member.setMemberPoints(member.getMemberPoints().subtract(amount));
        memberRepository.save(member); // 🔥 반드시 저장

        // ✅ 포인트 내역 저장
        PointHistory history = new PointHistory();
        history.setMember(member);
        history.setPoints(amount.negate()); // 사용 시 음수 값 저장
        history.setType(PointHistory.PointType.USE);
        history.setDescription(description);
        history.setCreatedAt(LocalDateTime.now());

        pointHistoryRepository.save(history);
    }

    // 특정 회원의 포인트 내역 조회
    public Page<PointHistory> getPointHistory(String memberId, Pageable pageable) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        return pointHistoryRepository.findByMember_MemberIdOrderByCreatedAtDesc(member.getMemberId(), pageable);
    }

    @Transactional
    public void addPoints(String memberId, BigDecimal amount, String description) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("회원 없음"));

        // ✅ 포인트 적립
        member.setMemberPoints(member.getMemberPoints().add(amount));
        memberRepository.save(member);

        // ✅ 포인트 적립 내역 저장
        PointHistory history = new PointHistory();
        history.setMember(member);
        history.setPoints(amount);
        history.setType(PointHistory.PointType.EARN);
        history.setDescription(description);
        history.setCreatedAt(LocalDateTime.now());
        pointHistoryRepository.save(history);
    }
}
