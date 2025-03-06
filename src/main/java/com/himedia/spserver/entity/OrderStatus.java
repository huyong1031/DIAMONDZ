package com.himedia.spserver.entity;

import com.fasterxml.jackson.annotation.JsonValue;

public enum OrderStatus {
    PENDING("결제 대기"),
    CONFIRMED("결제 완료"),
    SHIPPED("배송 중"),
    DELIVERED("배송 완료"),
    PURCHASE_CONFIRMED("구매 확정"),
    CANCELED("주문 취소"),
    RETURNED("반품 처리"),
    REFUNDED("환불 처리");


    private final String korean;

    OrderStatus(String korean) {
        this.korean = korean;
    }

    @JsonValue
    public String getKorean() {
        return korean;
    }
}

