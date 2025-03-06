package com.himedia.spserver.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class OrderRequestDTO {
    private String memberId;  // 유저 ID (Member)
    private Integer orderSeq;
    private Integer productSeq;  // 상품 번호 (Product)
    // private String productSize;  // 상품 사이즈
    private String sizeValue;  // 상품 옵션(자식 카테고리 별 옵션(사이즈, 길이 등)
    private Integer quantity;  // 상품 수량
    private BigDecimal totalPrice;  // 총 가격
    private String shippingAddress;  // 배송 주소
    private String name;
    private String phone;
    private String address;
    private String selectedRequest;
    private String customRequest;
    private boolean isDefaultAddress;
    private BigDecimal usedPoints; // ✅ 추가 (사용 포인트) // 훈식 추가



    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

}
