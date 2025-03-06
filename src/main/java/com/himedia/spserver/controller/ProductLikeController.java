package com.himedia.spserver.controller;

import com.himedia.spserver.entity.ProductLike;
import com.himedia.spserver.service.ProductLikeService;
import com.himedia.spserver.dto.ProductLikeRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/post")
public class ProductLikeController {

    @Autowired
    private ProductLikeService productLikeService;

    // 좋아요 추가/취소
    @PostMapping("/addLike")
    public ResponseEntity<String> addLike(@RequestBody ProductLikeRequest request) {
        try {
            // 로그인된 사용자와 상품을 기반으로 좋아요를 추가하거나 취소
            boolean isLiked = productLikeService.addOrRemoveLike(request.getMemberId(), request.getProductSeq());

            if (isLiked) {
                return ResponseEntity.ok("좋아요 추가 성공");
            } else {
                return ResponseEntity.ok("좋아요 취소 성공");
            }
        } catch (Exception e) {
            return ResponseEntity.status(500).body("서버 오류: " + e.getMessage());
        }
    }

    // 좋아요 /취소
    @DeleteMapping("/removeLike")
    public ResponseEntity<?> removeLike(@RequestParam String memberId, @RequestParam int productSeq) {
        boolean isDeleted  = productLikeService.removeLike(memberId, productSeq);
        if (isDeleted) {
            return ResponseEntity.ok("좋아요 취소 성공");
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("좋아요 취소 실패 또는 존재하지 않음");
        }
    }

    @GetMapping("/getUserLikes")
    public List<ProductLike> getUserLikes(@RequestParam String memberId, @RequestParam(required = false) Long productSeq) {
        // productSeq가 전달되었으면 해당 상품에 대한 좋아요 목록만 조회
        if (productSeq != null) {
            return productLikeService.getUserLikesByProductSeq(memberId, productSeq);  // 상품별로 좋아요 조회
        } else {
            // productSeq가 없다면 전체 좋아요 목록 조회
            return productLikeService.getUserLikes(memberId);  // 전체 상품에 대한 좋아요 목록 조회
        }
    }

}
