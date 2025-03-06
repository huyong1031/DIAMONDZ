package com.himedia.spserver.service;

import com.himedia.spserver.dto.OrdersResponseDTO;
import com.himedia.spserver.dto.Paging;
import com.himedia.spserver.dto.QnaDTO;
import com.himedia.spserver.entity.*;
import com.himedia.spserver.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class AdminService {
    @Autowired
    AdminRepository adminRepository;

    @Autowired
    QnaRepository qR;

    @Autowired
    OrdersRepository ordersRepository;

    @Autowired
    OrdersDetailRepository odR;

    public Admin getAdmin(String adminId) {
        Optional<Admin> admin = adminRepository.findByAdminId(adminId);
        return admin.orElse(null);
    }

    public HashMap<String, Object> getAdminMemberList(int page, String key, String searchType, String sortField, String sortOrder) {
        // 입력값 검증
        if (page < 1) page = 1;
        if (key != null) key = key.trim();

        // 정렬 필드 및 방향 설정
        if (sortField == null || sortField.trim().isEmpty()) {
            sortField = "inDate"; // 기본 정렬 필드는 가입일
        }

        Sort sort;
        if ("desc".equalsIgnoreCase(sortOrder)) {
            sort = Sort.by(Sort.Direction.DESC, sortField);
        } else {
            sort = Sort.by(Sort.Direction.ASC, sortField);
        }

        HashMap<String, Object> result = new HashMap<>();
        try {
            Pageable pageable = PageRequest.of(page - 1, 5, sort);
            Page<Member> memberPage;

            if (key == null || key.trim().isEmpty()) {
                memberPage = adminRepository.findAll(pageable);
            } else {
                if ("name".equals(searchType)) {
                    memberPage = adminRepository.findByMemberNameContaining(key, pageable);
                } else if ("id".equals(searchType)) {
                    memberPage = adminRepository.findByMemberIdContaining(key, pageable);
                } else {
                    memberPage = adminRepository.findByMemberNameContaining(key, pageable);
                }
            }

            List<Member> memberList = memberPage.getContent();
            int totalCount = (int) memberPage.getTotalElements();

            Paging paging = new Paging();
            paging.setPage(page);
            paging.setTotalCount(totalCount);
            paging.calPaging();

            result.put("memberList", memberList);
            result.put("paging", paging);
            result.put("key", key);
            result.put("sortField", sortField);
            result.put("sortOrder", sortOrder);

            return result;
        } catch (Exception e) {
            throw new RuntimeException("회원 목록 조회 중 오류 발생: " + e.getMessage());
        }
    }


    public List<Qna> getQnaList(int page, String key) { // ✅ 반환 타입 List<Qna> 로 변경
        Paging paging = new Paging();
        paging.setDisplayPage(10);
        paging.setDisplayRow(10);
        paging.setPage(page);

        Page<Qna> list; // Page<Qna> 선언

        if( key.equals("") || key == null){
            int count = qR.countAllBy();
            paging.setTotalCount(count);
            paging.calPaging();
            Pageable pageable = PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "inDate"));
            list = qR.findAll( pageable );
        }else{
            int count = qR.findBySubjectContaining(key).size();
            paging.setTotalCount(count);
            paging.calPaging();
            Pageable pageable = PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "inDate"));
            list = qR.findAllBySubjectContaining( key, pageable );
        }

        // 페이징 정보는 별도로 처리 (예: 컨트롤러에서 계산하거나, 별도 메서드에서 반환)
        return list.getContent(); // ✅ List<Qna> 만 반환
    }

    // 페이징 정보만 반환하는 별도 메서드 (선택 사항)
    public Paging getQnaPaging(int page, String key) {
        Paging paging = new Paging();
        paging.setDisplayPage(10);
        paging.setDisplayRow(10);
        paging.setPage(page);

        if( key.equals("") || key == null){
            int count = qR.countAllBy();
            paging.setTotalCount(count);
            paging.calPaging();
        }else{
            int count = qR.findBySubjectContaining(key).size();
            paging.setTotalCount(count);
            paging.calPaging();
        }

        return paging;
    }

    public Qna getQna(int qnaSeq) {
        return qR.findByQnaSeq( qnaSeq );
    }

    public void updateReply(String reply, int qnaSeq) {
        Qna qna = qR.findByQnaSeq( qnaSeq );
        qna.setReply( reply );
    }

    // 주문 목록 조회
    public List<Orders> getOrdersList(int page, String key) {
        Paging paging = new Paging();
        paging.setDisplayPage(10);
        paging.setDisplayRow(10);
        paging.setPage(page);
        Page<Orders> list;

        if (key.equals("") || key == null) {
            int count = ordersRepository.countAllBy();
            paging.setTotalCount(count);
            paging.calPaging();
            Pageable pageable = PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "orderDate"));
            // 여기서 JOIN FETCH를 사용하는 커스텀 쿼리 메서드 사용
            list = ordersRepository.findAllWithDetails(pageable);
        } else {
            // 검색 조건에 따른 쿼리 (회원 ID 또는 주문 번호로 검색)
            try {
                int orderSeq = Integer.parseInt(key);
                int count = ordersRepository.countByMember_MemberIdContainingOrOrderSeq(key, orderSeq);
                paging.setTotalCount(count);
                paging.calPaging();
                Pageable pageable = PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "orderDate"));
                list = ordersRepository.findByMember_MemberIdContainingOrOrderSeqWithDetails(key, orderSeq, pageable);
            } catch (NumberFormatException e) {
                int count = ordersRepository.countByMember_MemberIdContaining(key);
                paging.setTotalCount(count);
                paging.calPaging();
                Pageable pageable = PageRequest.of(page-1, 10, Sort.by(Sort.Direction.DESC, "orderDate"));
                list = ordersRepository.findByMember_MemberIdContainingWithDetails(key, pageable);
            }
        }

        return list.getContent();
    }


    // 주문 페이징 정보
    public Paging getOrdersPaging(int page, String key) {
        Paging paging = new Paging();
        paging.setDisplayPage(10);
        paging.setDisplayRow(10);
        paging.setPage(page);

        if (key.equals("") || key == null) {
            int count = ordersRepository.countAllBy();
            paging.setTotalCount(count);
            paging.calPaging();
        } else {
            try {
                int count = ordersRepository.countByMember_MemberIdContainingOrOrderSeq(key, Integer.parseInt(key));
                paging.setTotalCount(count);
                paging.calPaging();
            } catch (NumberFormatException e) {
                // 숫자로 변환할 수 없는 경우 회원 ID로만 검색
                int count = ordersRepository.countByMember_MemberIdContaining(key);
                paging.setTotalCount(count);
                paging.calPaging();
            }
        }

        return paging;
    }

    // 주문 상세 정보 조회 메서드
    public List<OrdersResponseDTO.OrderDetailInfo> getOrderDetails(int orderSeq) {
        List<OrdersDetail> details = odR.findByOrders_OrderSeq(orderSeq);
        return details.stream()
                .map(OrdersResponseDTO.OrderDetailInfo::new)
                .collect(Collectors.toList());
    }

    // 주문 상태 업데이트 메서드
    public void updateOrderStatus(int orderSeq, String status) {
        Orders order = ordersRepository.findByOrderSeqWithDetails(orderSeq)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        OrderStatus newStatus = OrderStatus.valueOf(status);
        order.setOrderStatus(newStatus);

        // 배송완료 상태로 변경될 때 배송완료 날짜 설정
        if (newStatus == OrderStatus.DELIVERED) {
            order.setDeliveryDate(LocalDateTime.now());
        }

        ordersRepository.save(order);
    }

    // 원본 OrdersDetail 엔티티를 반환하는 메서드
    public List<OrdersDetail> getOrderDetailsRaw(int orderSeq) {
        return odR.findByOrders_OrderSeq(orderSeq);
    }

    public OrdersResponseDTO getOrderWithDetails(int orderSeq) {
        Orders order = ordersRepository.findByOrderSeqWithDetails(orderSeq)
                .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

        OrdersResponseDTO responseDTO = new OrdersResponseDTO(order);
        List<OrdersDetail> details = odR.findByOrders_OrderSeq(orderSeq);
        responseDTO.setOrderDetailsFromEntity(details);

        return responseDTO;
    }

}
