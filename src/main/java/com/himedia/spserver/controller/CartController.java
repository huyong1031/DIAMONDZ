package com.himedia.spserver.controller;

import com.himedia.spserver.dto.CartDTO;
import com.himedia.spserver.entity.Cart;
import com.himedia.spserver.service.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    CartService cs;

    @PostMapping("/insertCart")
    public HashMap<String, Object> insertCart(
            @RequestParam("productSeq") int productSeq,
            @RequestParam("memberId") String memberId,
            @RequestParam("quantity") int quantity,
            @RequestParam("option") String option
    ) {
        HashMap<String, Object> result = new HashMap<>();
        cs.insertCart(productSeq, memberId, quantity, option);
        result.put("msg", "ok");
        return result;
    }

    @GetMapping("/getCartList")
    public ResponseEntity<List<CartDTO>> getCartList(@RequestParam String memberId) {
        List<CartDTO> cartDTOList = cs.getCartListByMember(memberId);
        return ResponseEntity.ok(cartDTOList);
    }

    @DeleteMapping("/deletecart/{cartSeq}")
    public ResponseEntity<?> deleteCart(@PathVariable("cartSeq") int cartSeq) {
        try {
            cs.deleteCart(cartSeq);
            return ResponseEntity.ok("장바구니 항목이 삭제되었습니다.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage()); // ✅ 존재하지 않으면 404 반환
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("장바구니 항목 삭제 중 오류 발생: " + e.getMessage());
        }
    }


}
