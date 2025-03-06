package com.himedia.spserver.service;

import com.himedia.spserver.dto.OrderRequestDTO;
import com.himedia.spserver.dto.OrdersResponseDTO;
import com.himedia.spserver.entity.*;
import com.himedia.spserver.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class OrdersService {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private ProductOptionRepository poR;

    @Autowired
    private OrdersDetailRepository odR;

    @Autowired
    PointService pointService;

    @Autowired
    CartRepository cartRepository;

    public Page<Orders> getPagedOrdersByMemberId(String memberId, Pageable pageable) {
        if (memberId == null || memberId.isEmpty()) {
            throw new IllegalArgumentException("memberId가 없습니다.");
        }

        boolean exists = memberRepository.existsById(memberId);
        if (!exists) {
            throw new IllegalArgumentException("해당 memberId에 대한 주문 내역이 존재하지 않습니다.");
        }

        // ✅ Product 정보까지 한 번에 가져오기 위해 JOIN FETCH 추가
        Pageable sortedPageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "orderDate"));

        // ✅ 기존 방식 유지: Page<Orders>를 가져옴
        Page<Orders> ordersPage = ordersRepository.findByMember_MemberIdWithProduct(memberId, sortedPageable);

        // ✅ 컨트롤러에서 변환할 수 있도록 그대로 반환 (변환은 컨트롤러에서 처리)
        return ordersPage;
    }


    public void createOrder(List<OrderRequestDTO> orderRequestDTO) {

        if (orderRequestDTO.isEmpty()) {
            throw new RuntimeException("주문 데이터가 없습니다.");
        }

        // ✅ 첫 번째 주문을 기준으로 주문 정보 생성
        OrderRequestDTO firstOrder = orderRequestDTO.get(0);
        Member member = memberRepository.findByMemberId(firstOrder.getMemberId())
                .orElseThrow(() -> new RuntimeException("Member not found"));

        // ✅ 전체 주문 총액 계산
        BigDecimal totalOrderPrice = orderRequestDTO.stream()
                .map(OrderRequestDTO::getTotalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // ✅ 사용자가 입력한 포인트 차감 처리 (추가) -------------------
        BigDecimal usedPoints = firstOrder.getUsedPoints();
        if (usedPoints != null && usedPoints.compareTo(BigDecimal.ZERO) > 0) {
            if (member.getMemberPoints().compareTo(usedPoints) < 0) {
                throw new RuntimeException("포인트 부족");
            }
            totalOrderPrice = totalOrderPrice.subtract(usedPoints);
            pointService.usePoints(member.getMemberId(), usedPoints, "상품 결제 사용");
        } // ------------------------------------------

        // ✅ 포인트 적립 (결제 금액의 3%)
        BigDecimal earnedPoints = totalOrderPrice.multiply(BigDecimal.valueOf(0.03)).setScale(0, RoundingMode.DOWN);
        if (earnedPoints.compareTo(BigDecimal.ZERO) > 0) {
            pointService.addPoints(member.getMemberId(), earnedPoints, "주문 결제 적립");
        }

        // ✅ Orders 객체 생성 (한 개의 주문 그룹)
        Orders order = Orders.builder()
                .member(member)
                .totalPrice(totalOrderPrice)
                .shippingAddress(firstOrder.getShippingAddress())
                .phone(firstOrder.getPhone()) // 주문 시 입력한 전화번호 저장
                .build();
        ordersRepository.save(order);

        // ✅ 주문된 상품 목록을 리스트로 저장
        List<Integer> orderedProductSeqs = new ArrayList<>();

        // ✅ 개별 상품 주문 저장 (OrdersDetail)
        for (OrderRequestDTO dto : orderRequestDTO) {
            Product product = productRepository.findById(dto.getProductSeq())
                    .orElseThrow(() -> new RuntimeException("Product not found"));

            ProductOption productOption = poR.findBySizeValue(dto.getSizeValue())
                    .orElseThrow(() -> new RuntimeException("ProductOption not found"));

            // 🔹 변경됨: 개별 상품 가격 계산 (단가 * 수량)
            BigDecimal unitPrice = new BigDecimal(product.getProductSalePrice());
            BigDecimal itemTotalPrice = unitPrice.multiply(BigDecimal.valueOf(dto.getQuantity())); // ✅ 개별 가격 적용

            OrdersDetail orderDetail = OrdersDetail.builder()
                    .orders(order) // 🔹 변경됨: **모든 주문 상세가 같은 Orders 객체에 묶이도록 수정**
                    .product(product)
                    .productOption(productOption)
                    .sizeValue(productOption.getSizeValue())  // ✅ sizeValue 직접 저장
                    .quantity(dto.getQuantity())
                    .price(itemTotalPrice) // 🔹 변경됨: **각 상품의 개별 가격 적용**
                    .build();

            odR.save(orderDetail);

            // ✅ 주문된 상품 리스트에 추가
            orderedProductSeqs.add(dto.getProductSeq());

            // ✅ [수정됨] 주문 완료 후 장바구니에서 해당 상품 삭제
            cartRepository.deleteByMemberIdAndProductSeq(member.getMemberId(), dto.getProductSeq());
            System.out.println("🛒 장바구니에서 삭제된 상품: " + dto.getProductSeq());
        }
    }
    public boolean confirmOrder(int orderSeq) {
        Optional<Orders> optionalOrder = ordersRepository.findById(orderSeq);

        if (optionalOrder.isPresent()) {
            Orders order = optionalOrder.get();
            System.out.println("🔍 현재 주문 상태: " + order.getOrderStatus()); // ✅ 로그 출력

            if (order.getOrderStatus() == OrderStatus.DELIVERED) {
                order.setOrderStatus(OrderStatus.PURCHASE_CONFIRMED);
                ordersRepository.save(order);
                System.out.println("✅ 주문번호 " + orderSeq + " 구매 확정 완료");
                return true;
            } else {
                System.out.println("🚨 구매 확정 실패: 주문번호 " + orderSeq + "는 배송 완료 상태가 아님 (" + order.getOrderStatus() + ")");
            }
        } else {
            System.out.println("🚨 구매 확정 실패: 주문번호 " + orderSeq + "를 찾을 수 없음");
        }
        return false;
    }

}
