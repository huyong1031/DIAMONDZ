package com.himedia.spserver.dto;

import com.himedia.spserver.entity.Cart;
import com.himedia.spserver.entity.Product;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartDTO {
    private int cartSeq;
    private int quantity;
    private String memberId;
    private String productName;
    private int productSalePrice; // ✅ 상품 가격 추가
    private String sizeValue; // ✅ 선택한 옵션(사이즈) 추가
    private int productSeq; // ✅ 상품 ID 추가 = > Cartlist 에서 해당 상품의 상세정보 조회를 위함
    private String productImage;

    public CartDTO(Cart cart) {
        this.cartSeq = cart.getCartSeq();
        this.quantity = cart.getQuantity();
        this.memberId = cart.getMember().getMemberId();
        this.productName = cart.getProduct().getProductName();
        this.productSeq = cart.getProduct().getProductSeq(); // ✅ 추가
        this.productImage = cart.getProductImage();

        if (cart.getProduct() != null) {
            this.productName = cart.getProduct().getProductName();
            this.productSalePrice = cart.getProduct().getProductSalePrice();
        } else {
            this.productName = "알 수 없는 상품";
            this.productSalePrice = 0;
        }
        // ✅ 선택한 옵션(사이즈) 추가
        if (cart.getProductOption() != null) {
            this.sizeValue = cart.getProductOption().getSizeValue();
        } else {
            this.sizeValue = "옵션 없음";
        }

    }
}
