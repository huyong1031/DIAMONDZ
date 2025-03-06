package com.himedia.spserver.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
@Transactional
@RequiredArgsConstructor
public class MemberMailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    // 이메일 인증 코드 저장 (임시 메모리)
    private final Map<String, String> emailVerificationMap = new HashMap<>();

    // 6자리 랜덤 인증 코드 생성
    private String generateVerificationCode() {
        Random random = new Random();
        int code = 100000 + random.nextInt(900000);
        return String.valueOf(code);
    }

    // 이메일로 인증 코드 전송
    public void sendVerificationEmail(String email) {
        String verificationCode = generateVerificationCode();
        emailVerificationMap.put(email, verificationCode.trim()); // 코드 저장 시 공백 제거

        MimeMessage message = mailSender.createMimeMessage();
        try {
            message.setFrom(senderEmail);
            message.setRecipients(MimeMessage.RecipientType.TO, email);
            message.setSubject("이메일 인증 코드");

            String body = "<h3>요청하신 인증 코드입니다.</h3>";
            body += "<h1>" + verificationCode + "</h1>";
            body += "<h3>감사합니다.</h3>";

            message.setText(body, "UTF-8", "html");
            mailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 실패", e);
        }
    }

    // 인증 코드 검증
    public boolean verifyCode(String email, String code) {
        if (!emailVerificationMap.containsKey(email)) {
            return false; // 이메일이 존재하지 않으면 검증 실패
        }

        String storedCode = emailVerificationMap.get(email).trim(); // 저장된 코드
        String inputCode = code.trim(); // 입력된 코드

        if (storedCode.equals(inputCode)) {
            emailVerificationMap.remove(email); // 인증 완료 후 삭제
            return true;
        }
        return false;
    }
}
