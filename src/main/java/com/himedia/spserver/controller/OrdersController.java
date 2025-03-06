package com.himedia.spserver.controller;

import com.himedia.spserver.dto.OrderRequestDTO;
import com.himedia.spserver.dto.OrdersResponseDTO;
import com.himedia.spserver.entity.*;
import com.himedia.spserver.repository.MemberRepository;
import com.himedia.spserver.repository.OrdersDetailRepository;
import com.himedia.spserver.repository.OrdersRepository;
import com.himedia.spserver.repository.ProductRepository;
import com.himedia.spserver.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/orders")
public class OrdersController {

    @Autowired
    private OrdersService ordersService;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    OrdersRepository ordersRepository;

    @Autowired
    private OrdersDetailRepository ordersDetailRepository;


    @GetMapping("/history/{memberId}")
    public ResponseEntity<?> getOrderHistory(
            @PathVariable String memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            // ✅ 요청값 검증: memberId가 비어 있는지 확인
            if (memberId == null || memberId.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("잘못된 요청: memberId가 필요합니다.");
            }

            Pageable pageable = PageRequest.of(page, size);

            // ✅ 주문 내역 조회 (500 오류 발생 가능 부분)
            Page<Orders> orders = ordersService.getPagedOrdersByMemberId(memberId, pageable);

            if (orders.isEmpty()) {
                return ResponseEntity.ok(Collections.emptyList()); // ✅ 빈 배열 반환
            }

            // ✅ DTO 변환 후 반환 (`sizeValue` 포함)
            List<OrdersResponseDTO> orderList = orders.getContent().stream()
                    .map(order -> {
                        OrdersResponseDTO dto = new OrdersResponseDTO(order);

                        // ✅ 주문 상세 정보 가져와서 `sizeValue` 포함
                        List<OrdersDetail> details = ordersDetailRepository.findByOrders_OrderSeq(order.getOrderSeq());
                        dto.setOrderDetailsFromEntity(details);

                        return dto;
                    })
                    .toList();
            return ResponseEntity.ok(orderList);
        } catch (IllegalArgumentException e) {
            log.error("❌ 잘못된 memberId 요청: {}", e.getMessage());
            return ResponseEntity.badRequest().body("잘못된 요청: " + e.getMessage());
        } catch (Exception e) {
            log.error("🚨 주문 내역 조회 중 오류 발생: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("서버 오류 발생: 관리자에게 문의하세요.");
        }
    }



    @PostMapping("/createOrders")
    public ResponseEntity<String> createOrder(@RequestBody List<OrderRequestDTO> orderRequestDTO) {
        try {
            System.out.println("📌 주문 요청 데이터: " + orderRequestDTO);
            ordersService.createOrder(orderRequestDTO);
            return ResponseEntity.ok("주문이 성공적으로 처리되었습니다.");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("주문 처리 중 오류가 발생했습니다.");
        }
    }

    @PostMapping("/confirm/{orderSeq}")
    public ResponseEntity<?> confirmOrder(@PathVariable int orderSeq) {
        System.out.println("🛠 구매 확정 요청 수신: 주문번호 " + orderSeq); // ✅ 로그 출력

        boolean success = ordersService.confirmOrder(orderSeq);

        if (success) {
            System.out.println("✅ 구매 확정 성공: 주문번호 " + orderSeq);
            return ResponseEntity.ok(Map.of("success", true, "message", "구매 확정 완료"));
        } else {
            System.out.println("🚨 구매 확정 실패: 주문번호 " + orderSeq);
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "구매 확정 실패"));
        }
    }

    @GetMapping("/{orderSeq}/order-products")
    public ResponseEntity<List<Map<String, Object>>> getOrderProducts(@PathVariable Integer orderSeq) {
        List<OrdersDetail> orderDetails = ordersRepository.findByOrders_OrderSeq(orderSeq);

        if (orderDetails.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.emptyList());
        }

        List<Map<String, Object>> response = new ArrayList<>();
        for (OrdersDetail detail : orderDetails) {
            Map<String, Object> productInfo = new HashMap<>();
            productInfo.put("orderSeq", detail.getOrders().getOrderSeq()); // ✅ 주문번호 포함
            productInfo.put("productSeq", detail.getProduct().getProductSeq()); // ✅ 상품번호 포함
            productInfo.put("productName", detail.getProduct().getProductName()); // ✅ 상품 이름 포함
            productInfo.put("imageUrl", detail.getProduct().getProductImage()); // ✅ 상품 이미지 포함

            response.add(productInfo);
        }

        return ResponseEntity.ok(response);
    }

}
