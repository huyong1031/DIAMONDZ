package com.himedia.spserver.controller;

import com.himedia.spserver.service.MemberMailService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:3000") // React에서 요청 허용
@RestController
@RequestMapping("/member/auth")
@RequiredArgsConstructor
public class MemberMailController {


    private final MemberMailService memberMailService;

    // 이메일 인증 코드 전송 API
    @PostMapping("/send-email")
    public Map<String, Object> sendEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");

        // 이메일 형식 검증
        if (email == null || !email.contains("@") || !email.contains(".")) {
            throw new RuntimeException("올바른 이메일 주소를 입력해주세요.");
        }

        memberMailService.sendVerificationEmail(email);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "인증 코드가 이메일로 전송되었습니다.");
        return response;
    }


    // 이메일 인증 코드 검증 API
    @PostMapping("/verify-email")
    public Map<String, Object> verifyEmail(@RequestBody Map<String, String> request) {
        String email = request.get("email");
        String code = request.get("code");

        System.out.println("인증 요청 이메일: " + email);
        System.out.println("인증 요청 코드: " + code);

        boolean isVerified = memberMailService.verifyCode(email, code);

        Map<String, Object> response = new HashMap<>();
        response.put("success", isVerified);
        response.put("message", isVerified ? "이메일 인증이 완료되었습니다." : "인증 코드가 일치하지 않습니다.");
        return response;
    }
}
