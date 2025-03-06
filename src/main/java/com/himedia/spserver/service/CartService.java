package com.himedia.spserver.service;

import com.himedia.spserver.dto.CartDTO;
import com.himedia.spserver.entity.*;
import com.himedia.spserver.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CartService {

    @Autowired
    ProductRepository pR;
    @Autowired
    MemberRepository mR;
    @Autowired
    CartRepository cartR;
    @Autowired
    ProductOptionRepository poR;


    public void insertCart(int productSeq, String memberId, int quantity, String option) {
        System.out.println("🟢 장바구니 추가 요청: productSeq=" + productSeq + ", option=" + option + ", 수량=" + quantity);

        // 회원 조회
        Member member = mR.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("해당 ID의 회원을 찾을 수 없습니다: " + memberId));

        Product product = pR.findById(productSeq)
                .orElseThrow(() -> new IllegalArgumentException("해당 productSeq에 해당하는 상품이 없습니다."));

        // 옵션 조회 (기존 옵션 테이블 활용)
        ProductOption productOption = poR.findBySizeValue(option)
                .orElseThrow(() -> new IllegalArgumentException("해당 옵션을 찾을 수 없습니다: " + option));

        Cart cart = new Cart();
        cart.setQuantity(quantity);
        cart.setMember(member);
        cart.setProduct(product);
        cart.setProductOption(productOption);
        cartR.save(cart);
    }

    public List<CartDTO> getCartListByMember(String memberId) {
        List<Cart> cartList = cartR.findByMemberMemberId(memberId);

        // ✅ Cart 엔티티를 CartDTO로 변환
        return cartList.stream()
                .map(CartDTO::new)
                .collect(Collectors.toList());
    }



    public HashMap<String, Object> getCartList(String memberId) {
        HashMap<String, Object> result = new HashMap<>();
        List<Cart> list = cartR.findByMemberMemberId(memberId);   // 3

        result.put("cartList", list);
        int totalPrice = 0;
        for (Cart cart : list) {
            totalPrice += (cart.getProduct().getProductSalePrice() * cart.getQuantity());
        }
        result.put("totalPrice", totalPrice);
        return result;


    }

    public List<Cart> getAllCarts() {
        return cartR.findAll(); // 전체 카트 조회
    }

    public void deleteCart(int cartSeq) {
        Optional<Cart> cart = cartR.findByCartSeq( cartSeq );
        if (cart == null) { // ✅ 존재하지 않으면 예외 처리
            throw new IllegalArgumentException("해당 cartSeq에 해당하는 장바구니 항목이 존재하지 않습니다: " + cartSeq);
        }
        cartR.delete(cart.get()); // ✅ null이 아닐 때만 삭제
    }

//    public List<Cart> getCartListByMember(String memberId) {
//        return cartR.findByMemberMemberId(memberId);
//    }
}
