package com.himedia.spserver.controller;

import com.himedia.spserver.entity.Member;
import com.himedia.spserver.entity.PointHistory;
import com.himedia.spserver.repository.MemberRepository;
import com.himedia.spserver.repository.PointHistoryRepository;
import com.himedia.spserver.service.PointService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;

@RestController
@RequestMapping("/api/points")
public class PointController {

    @Autowired
    private PointService pointService;

    @Autowired
    private MemberRepository memberRepository; // 🔥 @Autowired 추가

    @Autowired
    private PointHistoryRepository pointHistoryRepository;

    // 포인트 적립 API
    @PostMapping("/earn")
    public ResponseEntity<String> earnPoints(
            @RequestParam String memberId,
            @RequestParam BigDecimal amount,
            @RequestParam String description) {
        pointService.earnPoints(memberId, amount, description);
        return ResponseEntity.ok("포인트 적립 완료");
    }

    // 포인트 사용 API
    @PostMapping("/use")
    public ResponseEntity<String> usePoints(
            @RequestParam String memberId,
            @RequestParam BigDecimal amount,
            @RequestParam String description) {
        pointService.usePoints(memberId, amount, description);
        return ResponseEntity.ok("포인트 사용 완료");
    }

    // 포인트 내역 조회 API
    @GetMapping("/history/{memberId}")
    public ResponseEntity<Page<Map<String, Object>>> getPointHistory(
            @PathVariable String memberId,
            Pageable pageable) {

        Page<PointHistory> historyPage = pointService.getPointHistory(memberId, pageable);

        Page<Map<String, Object>> responsePage = historyPage.map(history -> {
            Map<String, Object> data = new HashMap<>();
            data.put("date", history.getCreatedAt().toString());
            data.put("description", history.getDescription());
            data.put("points", history.getPoints());
            data.put("type", history.getType().name());
            data.put("isEarn", history.getType() == PointHistory.PointType.EARN);
            return data;
        });

        return ResponseEntity.ok(responsePage);
    }
}
