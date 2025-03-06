package com.himedia.spserver.service;

import com.himedia.spserver.entity.ProductLike;
import com.himedia.spserver.entity.Member;
import com.himedia.spserver.entity.Product;
import com.himedia.spserver.repository.ProductLikeRepository;
import com.himedia.spserver.repository.MemberRepository;
import com.himedia.spserver.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductLikeService {

    @Autowired
    private ProductLikeRepository productLikeRepository;

    @Autowired
    private MemberRepository memberRepository;  // MemberRepository를 추가하여 member를 조회

    @Autowired
    private ProductRepository productRepository;  // ProductRepository를 추가하여 product를 조회

    // 좋아요 추가/취소 로직
    public boolean addOrRemoveLike(String memberId, int productSeq) {
        // Member 객체 조회
        Member member = memberRepository.findById(memberId).orElseThrow(() -> new RuntimeException("Member not found"));

        // Product 객체 조회
        Product product = productRepository.findById(productSeq).orElseThrow(() -> new RuntimeException("Product not found"));

        // 기존에 좋아요가 있는지 확인
        ProductLike existingLike = productLikeRepository.findByMemberMemberIdAndProductProductSeq(memberId, productSeq);

        if (existingLike != null) {
            // 이미 좋아요가 있다면 취소
            productLikeRepository.delete(existingLike);
            return false; // 좋아요가 취소됨
        } else {
            // 좋아요가 없다면 추가
            ProductLike newLike = new ProductLike();
            newLike.setMember(member);  // Member 객체를 설정
            newLike.setProduct(product);  // Product 객체를 설정
            productLikeRepository.save(newLike);
            return true; // 좋아요가 추가됨
        }
    }


    // 특정 상품에 대한 좋아요 목록을 가져오는 메서드
    public List<ProductLike> getUserLikesByProductSeq(String memberId, Long productSeq) {
        // memberId와 productSeq로 좋아요를 조회
        return productLikeRepository.findByMember_MemberIdAndProduct_ProductSeq(memberId, productSeq);
    }

    // 전체 좋아요 목록을 가져오는 메서드
    public List<ProductLike> getUserLikes(String memberId) {
        // memberId로 모든 좋아요 목록을 조회
        return productLikeRepository.findByMember_MemberId(memberId);
    }

    public boolean removeLike(String memberId, int productSeq) {
        ProductLike existingLike = productLikeRepository.findByMemberMemberIdAndProductProductSeq(memberId, productSeq);
        if (existingLike != null) {
            System.out.println("✅ 좋아요 데이터 삭제됨: " + existingLike);
            productLikeRepository.delete(existingLike);
            return true; // 삭제 성공
        }
        System.out.println("❌ 좋아요 데이터가 존재하지 않음: memberId=" + memberId + ", productSeq=" + productSeq);
        return false; // 삭제할 데이터 없음
    }
}
