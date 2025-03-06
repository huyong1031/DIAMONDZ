package com.himedia.spserver.controller;

import com.himedia.spserver.entity.*;
import com.himedia.spserver.repository.*;
import com.himedia.spserver.service.ProductService;
import com.himedia.spserver.service.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/review")
public class ReviewController {

    @Autowired
    ReviewService rvs;

    @Autowired
    ProductService ps;

    @Autowired
    ReviewRepository reviewRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    OrdersDetailRepository ordersDetailRepository;

    @GetMapping("/getReview")
    public HashMap<String, Object> getReview(@RequestParam("productSeq") int productSeq) {
        HashMap<String, Object> result = new HashMap<>();

        // productSeq에 해당하는 제품을 가져옵니다.
        List<Product> products = ps.getProduct(productSeq);
        List<Review> reviews = rvs.getReview(productSeq);
        double averageRating = rvs.getAverageRating(productSeq);

        // 결과가 없으면 null을 반환
        if (products == null || products.isEmpty()) {  // null 체크 추가
            result.put("product", null);
        } else {
            result.put("product", products.get(0));
        }


        if (reviews.isEmpty()) {
            result.put("review", null);
            result.put("reviewCount", 0); // 리뷰가 없으면 갯수는 0
            result.put("averageRating", 0); // 평균 별점이 없으면 0
        } else {
            result.put("review", reviews);
            result.put("reviewCount", reviews.size()); // 리뷰 갯수
            result.put("averageRating", averageRating); // 평균 별점
        }

        return result;
    }

    // 로그인한 사용자가 작성한 리뷰 조회 (페이징 적용)
    @GetMapping("/my")
    public ResponseEntity<Page<Review>> getMyReviews(
            @RequestParam("memberId") String memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "indate"));
        Page<Review> reviews = rvs.getUserReviews(memberId, pageable);

        return ResponseEntity.ok(reviews);
    }


    @GetMapping("/{reviewSeq}")
    public ResponseEntity<?> getReviewById(@PathVariable Integer reviewSeq) {
        Optional<Review> review = rvs.getReviewById(reviewSeq);

        if (review.isPresent()) {
            return ResponseEntity.ok(review.get());
        } else {
            return ResponseEntity.status(404).body("해당 리뷰를 찾을 수 없습니다.");
        }
    }
    // ✅ 리뷰 저장 (ordersDetail 없이 orderSeq 직접 저장)
    @PostMapping("/save")
    public ResponseEntity<?> saveReview(@RequestBody Map<String, Object> request) {
        Integer orderSeq = (Integer) request.get("orderSeq");
        Integer productSeq = (Integer) request.get("productSeq");
        String reviewContent = (String) request.get("reviewContent");
        Integer reviewRating = (Integer) request.get("reviewRating");
        String memberId = (String) request.get("memberId");

        if (orderSeq == null || productSeq == null || reviewContent == null || memberId == null) {
            return ResponseEntity.badRequest().body("🚨 필수 값이 누락되었습니다.");
        }

        // 🔍 기존 리뷰 존재 여부 확인 (orderSeq 직접 사용)
        boolean reviewExists = reviewRepository.existsByOrderSeqAndProduct_ProductSeq(orderSeq, productSeq);
        if (reviewExists) {
            return ResponseEntity.badRequest().body("이미 해당 주문의 상품에 대한 리뷰가 존재합니다.");
        }

        // ✅ 주문 상세 조회 없이 orderSeq 직접 저장
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new RuntimeException("회원 정보를 찾을 수 없습니다."));

        Review review = new Review();
        review.setOrderSeq(orderSeq); // ✅ orderSeq 직접 저장
        review.setMember(member);
        review.setProduct(new Product()); // 필요 시 product 엔티티 조회
        review.getProduct().setProductSeq(productSeq);
        review.setReviewContent(reviewContent);
        review.setReviewRating(reviewRating);

        reviewRepository.save(review);

        return ResponseEntity.ok().body(Map.of("success", true, "message", "리뷰가 성공적으로 저장되었습니다."));
    }

    // ✅ 리뷰 중복 검사 (orderSeq 직접 사용)
    @GetMapping("/check")
    public ResponseEntity<?> checkReviewExists(
            @RequestParam("orderSeq") Integer orderSeq,
            @RequestParam("productSeq") Integer productSeq
    ) {
        if (orderSeq == null || productSeq == null) {
            return ResponseEntity.badRequest().body(Map.of("exists", false, "message", "🚨 orderSeq 또는 productSeq가 누락되었습니다."));
        }

        boolean reviewExists = reviewRepository.existsByOrderSeqAndProduct_ProductSeq(orderSeq, productSeq);

        return ResponseEntity.ok(Map.of("exists", reviewExists));
    }
}
