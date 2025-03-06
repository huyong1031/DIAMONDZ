package com.himedia.spserver.dto;

import com.himedia.spserver.entity.Orders;
import com.himedia.spserver.entity.OrdersDetail;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
public class OrdersResponseDTO {
    private int orderSeq;
    private String memberId;
    private String sizeValue;
    private Integer quantity;
    private Timestamp inDate;
    private Timestamp orderDate;
    private BigDecimal finalPrice;
    private String orderStatus;
    private String productName;
    private String productImageUrl;
    private String shippingAddress;
    private String phone; // 연락처 필드 추가
    private Timestamp deliveryDate; // 배송완료 날짜 필드 추가

    // 주문 상세 정보를 담을 리스트 추가
    private List<OrderDetailInfo> orderDetails;

    public OrdersResponseDTO(Orders order) {
        this.orderSeq = order.getOrderSeq();

        // Member null 체크
        if (order.getMember() != null) {
            this.memberId = order.getMember().getMemberId();
        } else {
            this.memberId = "회원 정보 없음";
        }

        // 연락처 설정
        if (order.getPhone() != null) {
            this.phone = order.getPhone();
        }

        this.sizeValue = order.getSizeValue();
        this.quantity = order.getQuantity();
        this.finalPrice = order.getTotalPrice();
        this.orderDate = Timestamp.valueOf(order.getOrderDate());

        // 배송완료 날짜 설정
        if (order.getDeliveryDate() != null) {
            this.deliveryDate = Timestamp.valueOf(order.getDeliveryDate());
        }

        // OrderStatus null 체크
        if (order.getOrderStatus() != null) {
            this.orderStatus = order.getOrderStatus().getKorean();
        } else {
            this.orderStatus = "상태 정보 없음";
        }

        // Product null 체크
        if (order.getProduct() != null) {
            this.productName = order.getProduct().getProductName();
            this.productImageUrl = order.getProduct().getProductImage();
        } else {
            this.productName = "상품 정보 없음";
            this.productImageUrl = "";
        }

        this.shippingAddress = order.getShippingAddress();
    }

    // 주문 상세 정보를 설정하는 메서드 추가
    public void setOrderDetailsFromEntity(List<OrdersDetail> details) {
        if (details != null) {
            this.orderDetails = details.stream()
                    .map(OrderDetailInfo::new)
                    .collect(Collectors.toList());
        }
    }

    // 주문 상세 정보를 담을 내부 클래스
    @Getter
    @Setter
    public static class OrderDetailInfo {
        private int ordersDetailSeq;
        private int productSeq;
        private String productName;
        private String productImageUrl;
        private int quantity;
        private BigDecimal price;
        private String sizeValue; // ✅ 추가(ordersDetail 에 옵션정보 추가를 위함)


        public OrderDetailInfo(OrdersDetail detail) {
            this.ordersDetailSeq = detail.getOrdersDetailSeq();
            this.quantity = detail.getQuantity();
            this.sizeValue = (detail.getProductOption() != null) ? detail.getProductOption().getSizeValue() : "사이즈 정보 없음"; // ✅ 추가
            if (detail.getPrice() != null) {
                this.price = detail.getPrice();
            }

            if (detail.getProduct() != null) {
                this.productSeq = detail.getProduct().getProductSeq();
                this.productName = detail.getProduct().getProductName();
                this.productImageUrl = detail.getProduct().getProductImage();
            }
        }
    }
}
