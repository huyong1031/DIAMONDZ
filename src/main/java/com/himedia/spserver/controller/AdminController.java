package com.himedia.spserver.controller;

import com.himedia.spserver.dto.OrdersResponseDTO;
import com.himedia.spserver.dto.Paging;
import com.himedia.spserver.dto.QnaDTO;
import com.himedia.spserver.entity.*;
import com.himedia.spserver.repository.OrdersRepository;
import com.himedia.spserver.service.AdminService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private OrdersRepository ordersRepository;

    @PostMapping("/loginAdmin")
    public ResponseEntity<Map<String, Object>> loginAdmin(@RequestBody Admin admin, HttpSession session) {
        Map<String, Object> result = new HashMap<>();
        Admin loginAdmin = adminService.getAdmin(admin.getAdminId());

        System.out.println("DB 저장된 아이디: " + (loginAdmin != null ? loginAdmin.getAdminId() : "사용자 없음"));
        System.out.println("사용자가 입력한 아이디: " + admin.getAdminId());
        System.out.println("DB 저장된 비밀번호: " + (loginAdmin != null ? loginAdmin.getAdminPwd() : "사용자 없음"));
        System.out.println("사용자가 입력한 비밀번호: " + admin.getAdminPwd());

        if (loginAdmin == null) {
            result.put("msg", "아이디를 확인하세요");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        } else if (!loginAdmin.getAdminPwd().equals(admin.getAdminPwd())) {
            result.put("msg", "패스워드를 확인하세요");
            return new ResponseEntity<>(result, HttpStatus.UNAUTHORIZED);
        } else {
            result.put("msg", "ok");
            session.setAttribute("loginAdmin", loginAdmin.getAdminId());
            result.put("loginAdmin", loginAdmin);
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
    }

    @GetMapping("/logoutAdmin")
    public ResponseEntity<?> logoutAdmin(HttpSession session, HttpServletResponse response) {
        session.invalidate();

        Cookie adminUserCookie = new Cookie("adminLogin", null);
        adminUserCookie.setPath("/");
        adminUserCookie.setMaxAge(0);
        response.addCookie(adminUserCookie);

        System.out.println("서버 세션 & adminLogin 쿠키 삭제 완료 ✅");
        return ResponseEntity.ok(Collections.singletonMap("msg", "success"));
    }

    @GetMapping("/member/getMemberList")
    public ResponseEntity<Map<String, Object>> getAdminMemberList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "key", required = false) String key,
            @RequestParam(value = "searchType", defaultValue = "name") String searchType,
            @RequestParam(value = "sortField", defaultValue = "inDate") String sortField,
            @RequestParam(value = "sortOrder", defaultValue = "desc") String sortOrder
    ) {
        HashMap<String, Object> result = adminService.getAdminMemberList(page, key, searchType, sortField, sortOrder);
        return ResponseEntity.ok(result);
    }


    @GetMapping("/getQnaList")
    public ResponseEntity<Map<String, Object>> getQnaList(
            @RequestParam("page") int page,
            @RequestParam(value = "key", required = false) String key
    ) {
        if (key == null) key = "";
        Map<String, Object> result = new HashMap<>();

        List<Qna> qnaList = adminService.getQnaList(page, key); // ✅ 서비스에서 List<Qna> 직접 받기
        Paging paging = adminService.getQnaPaging(page, key); // ✅ 서비스에서 페이징 정보 가져오기

        // 2. 엔티티 리스트를 DTO 리스트로 변환 (기존 코드 유지)
        List<QnaDTO> qnaDTOList = qnaList.stream()
                .map(QnaDTO::new)
                .collect(Collectors.toList());

        result.put("qnaList", qnaDTOList);
        result.put("paging", paging); // ✅ 페이징 정보 result 맵에 담기
        result.put("key", key);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/getQna")
    public ResponseEntity<Map<String, Object>> getQna(@RequestParam("qnaSeq") int qnaSeq) {
        Map<String, Object> result = new HashMap<>();
        Qna qna = adminService.getQna(qnaSeq);
        QnaDTO qnaDTO = new QnaDTO(qna);

        result.put("qna", qnaDTO); // "qna"라는 키로 변경
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/writeReply")
    public HashMap<String, Object> writeReply(@RequestParam("reply") String reply, @RequestParam("qnaSeq") int qnaSeq) {
        HashMap<String, Object> result = new HashMap<>();
        adminService.updateReply(reply, qnaSeq);
        result.put("msg", "ok");
        return result; // 이 메서드는 데이터 반환보다는 msg 전달이 목적이므로 DTO 적용 생략 (필요하다면 DTO 적용 가능)
    }

    @GetMapping("/getOrdersList")
    public ResponseEntity<Map<String, Object>> getOrdersList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "key", required = false) String key
    ) {
        if (key == null) key = "";
        Map<String, Object> result = new HashMap<>();

        // 주문 목록과 상세 정보를 함께 가져오기
        List<Orders> ordersList = adminService.getOrdersList(page, key);
        Paging paging = adminService.getOrdersPaging(page, key);

        // Orders 엔티티를 DTO로 변환하고 상세 정보도 함께 설정
        List<OrdersResponseDTO> ordersResponseDTOList = ordersList.stream()
                .map(order -> {
                    OrdersResponseDTO dto = new OrdersResponseDTO(order);
                    // 각 주문의 상세 정보도 함께 로드
                    List<OrdersDetail> details = adminService.getOrderDetailsRaw(order.getOrderSeq());
                    dto.setOrderDetailsFromEntity(details);
                    return dto;
                })
                .collect(Collectors.toList());

        result.put("ordersList", ordersResponseDTOList);
        result.put("paging", paging);
        result.put("key", key);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/getOrderDetails")
    public ResponseEntity<Map<String, Object>> getOrderDetails(@RequestParam("orderSeq") int orderSeq) {
        Map<String, Object> result = new HashMap<>();
        // 주문 상세 정보 가져오기
        List<OrdersResponseDTO.OrderDetailInfo> orderDetails = adminService.getOrderDetails(orderSeq);
        result.put("orderDetails", orderDetails);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/updateOrderStatus")
    public ResponseEntity<Map<String, Object>> updateOrderStatus(
            @RequestParam("orderSeq") int orderSeq,
            @RequestParam("status") String status
    ) {
        Map<String, Object> result = new HashMap<>();
        try {
            // OrderStatus Enum 값 검증
            OrderStatus orderStatus;
            try {
                orderStatus = OrderStatus.valueOf(status);
            } catch (IllegalArgumentException e) {
                result.put("msg", "유효하지 않은 주문 상태: " + status);
                return new ResponseEntity<>(result, HttpStatus.BAD_REQUEST);
            }

            adminService.updateOrderStatus(orderSeq, status);
            result.put("msg", "ok");
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            result.put("msg", "error: " + e.getMessage());
            return new ResponseEntity<>(result, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/getOrderInfo")
    public ResponseEntity<OrdersResponseDTO> getOrderInfo(@RequestParam("orderSeq") int orderSeq) {
        try {
            // 주문 기본 정보 조회 (전화번호 포함)
            Orders order = ordersRepository.findById(orderSeq)
                    .orElseThrow(() -> new RuntimeException("주문을 찾을 수 없습니다."));

            OrdersResponseDTO orderInfo = new OrdersResponseDTO(order);
            return new ResponseEntity<>(orderInfo, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}