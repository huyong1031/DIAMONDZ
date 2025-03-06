package com.himedia.spserver.service;

import com.himedia.spserver.entity.Member;
import com.himedia.spserver.entity.Product;
import com.himedia.spserver.entity.Review;
import com.himedia.spserver.repository.MemberRepository;
import com.himedia.spserver.repository.ProductRepository;
import com.himedia.spserver.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReviewService {

    @Autowired
    ReviewRepository rvR;

    @Autowired
    ProductRepository productRepository;

    @Autowired
    MemberRepository memberRepository;

    public List<Review> getReview(int productSeq) {
        return rvR.findByProductProductSeq(productSeq);
    }

    public double getAverageRating(int productSeq) {
        List<Review> reviews = rvR.findByProductProductSeq(productSeq);
        if (reviews.isEmpty()) {
            return 0; // 리뷰가 없으면 0을 반환
        }

        double totalRating = 0;
        for (Review review : reviews) {
            totalRating += review.getReviewRating(); // 리뷰 별점 합산
        }

        return totalRating / reviews.size(); // 평균 별점 계산
    }

    public Page<Review> getUserReviews(String memberId, Pageable pageable) {
        return rvR.findByMemberMemberId(memberId, pageable);
    }

    public Optional<Review> getReviewById(Integer reviewSeq) {
        return rvR.findById(reviewSeq);
    }

    public boolean saveReview(String memberId, int productSeq, String reviewContent, int reviewRating) {
        Optional<Product> productOpt = productRepository.findById(productSeq);
        Optional<Member> memberOpt = memberRepository.findByMemberId(memberId);

        if (productOpt.isEmpty() || memberOpt.isEmpty()) {
            return false;  // 상품 또는 회원 정보가 없으면 저장 불가
        }

        Review review = new Review();
        review.setProduct(productOpt.get());
        review.setMember(memberOpt.get());
        review.setReviewContent(reviewContent);
        review.setReviewRating(reviewRating);
        rvR.save(review);
        return true;
    }


}


