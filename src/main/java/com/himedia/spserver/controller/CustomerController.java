package com.himedia.spserver.controller;

import com.himedia.spserver.dto.QnaDTO;
import com.himedia.spserver.entity.Qna;
import com.himedia.spserver.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;

@RestController
@RequestMapping("/customer")
public class CustomerController {

    @Autowired
    private CustomerService cs;

    @GetMapping("/qnaList")
    public HashMap<String, Object> qnaList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam("memberId") String memberId) {
        if (page < 1) {
            page = 1; // 🔥 페이지 값이 1보다 작으면 기본값 1로 설정
        }
        System.out.println("📌 요청된 페이지: " + page);  // 🔥 서버에서 page 값 확인
        HashMap<String, Object> result = cs.getQnaList(page, memberId);
        return result;
    }


    @PostMapping("/confirmPass")
    public HashMap<String, Object> confirmPass(@RequestBody HashMap<String, Object> requestData) {
        System.out.println("📌 confirmPass 요청 받음: " + requestData);
        HashMap<String, Object> result = new HashMap<>();

        try {
            // ✅ 요청된 데이터에서 qnaSeq, pass 가져오기 (문자열로 전달될 가능성이 있어 변환)
            int qnaSeq;
            if (requestData.get("qnaSeq") instanceof Integer) {
                qnaSeq = (int) requestData.get("qnaSeq");
            } else {
                qnaSeq = Integer.parseInt(requestData.get("qnaSeq").toString().trim());  // 🔹 String → Integer 변환 (trim 추가)
            }

            // ✅ 비밀번호 값이 존재하는지 확인하고 가져오기
            String pass = requestData.get("pass") != null ? requestData.get("pass").toString().trim() : null;

            // ✅ QnA 데이터 조회 (회원 / 비회원 모두 조회 가능)
            QnaDTO qna = cs.getQnaWithoutMember(qnaSeq);

            // ✅ QnA 데이터를 찾을 수 없는 경우 오류 반환
            if (qna == null) {
                System.out.println("⚠️ QnA 데이터를 찾을 수 없습니다: qnaSeq = " + qnaSeq);
                result.put("msg", "not_found");
                return result;
            }

            // ✅ 비밀글이면 비밀번호 검증 필요
            if ("Y".equals(qna.getSecurity())) {
                System.out.println("🔍 QnA 비밀번호 확인: 저장된 비밀번호 = " + qna.getPass());

                // ✅ 비밀번호가 없는 경우 오류 반환
                if (qna.getPass() == null || pass == null) {
                    System.out.println("❌ 입력된 비밀번호가 없음");
                    result.put("msg", "fail");
                    return result;
                }

                // ✅ 비밀번호 검증
                if (!pass.equals(qna.getPass().trim())) {  // 🔹 trim() 제거 (이미 처리됨)
                    System.out.println("❌ 비밀번호 불일치: 입력된 pass = " + pass);
                    result.put("msg", "fail");
                } else {
                    System.out.println("✅ 비밀번호 일치");
                    result.put("msg", "ok");
                }
            } else {
                // ✅ 공개글이면 비밀번호 없이 확인 가능
                result.put("msg", "ok");
            }

        } catch (Exception e) {
            System.out.println("❌ confirmPass 예외 발생: " + e.getMessage());
            result.put("msg", "error");
        }

        return result;
    }


    @GetMapping("/getQna")
    public HashMap<String, Object> getQna(@RequestParam("qnaSeq") int qnaSeq,
                                          @RequestParam(value = "memberId", required = false) String memberId,
                                          @RequestParam(value = "pass", required = false) String pass) {
        System.out.println("📌 getQna 요청 받음: qnaSeq = " + qnaSeq + ", memberId = " + memberId);
        HashMap<String, Object> result = new HashMap<>();

        // ✅ 비회원도 조회할 수 있도록 수정
        QnaDTO qna = cs.getQnaWithoutMember(qnaSeq);

        if (qna == null) {
            result.put("error", "QnA not found");  // 🔹 QnA 데이터가 없으면 에러 반환 (기존 기능 유지)
            return result;
        }

        // ✅ 비밀글이면 작성자 포함 모든 사용자에게 비밀번호 입력 요구
        if ("Y".equals(qna.getSecurity())) {
            if (pass == null) {
                result.put("error", "password_required"); // 🔹 비밀번호 입력 요청
                return result;
            }
            if (!pass.equals(qna.getPass())) {
                result.put("error", "Invalid password"); // 🔹 비밀번호가 틀리면 오류 반환
                return result;
            }
        }

        result.put("qna", qna);  // 🔹 QnA 데이터 반환 (비밀번호가 맞거나 공개글인 경우)
        return result;
    }


    @GetMapping("/qnaPage")
    public HashMap<String, Object> getQnaPage(@RequestParam("qnaSeq") int qnaSeq, @RequestParam("memberId") String memberId) {
        HashMap<String, Object> result = new HashMap<>();
        int page = cs.getQnaPage(qnaSeq, memberId);
        result.put("page", page);
        return result;
    }

    @PostMapping("/writeQna")
    public HashMap<String, Object> writeQna( @RequestBody QnaDTO qnaDTO){
        HashMap<String, Object> result = new HashMap<>();
        cs.insertQna( qnaDTO );
        result.put("msg", "ok");
        return result;
    }

    @GetMapping("/allQnaList") // 모든 QnA 내역 가져오기
    public HashMap<String, Object> getAllQnaList(@RequestParam(value = "page", defaultValue = "1") int page) {
        if (page < 1) {
            page = 1;
        }
        System.out.println("📌 전체 QnA 목록 요청, 페이지: " + page);

        HashMap<String, Object> result = cs.getAllQnaList(page);

        // ✅ guestName도 포함하여 반환
        List<QnaDTO> qnaList = (List<QnaDTO>) result.get("qnaList");
        for (QnaDTO qna : qnaList) {
            if (qna.getMemberId() == null || "guest".equals(qna.getMemberId())) {
                qna.setMemberId(qna.getGuestName()); // 🔹 비회원이면 guestName을 memberId로 설정
            }
        }

        return result;
    }





}
