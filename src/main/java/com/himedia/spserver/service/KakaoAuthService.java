package com.himedia.spserver.service;

import com.himedia.spserver.entity.Member;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class KakaoAuthService {

    private final RestTemplate restTemplate;
    private final MemberService memberService;

    @Autowired
    public KakaoAuthService(RestTemplate restTemplate, MemberService memberService) {
        this.restTemplate = restTemplate;
        this.memberService = memberService;
    }

    public Map<String, Object> kakaoLogin(String code) {
        String accessToken = getKakaoAccessToken(code);

        if (accessToken == null) {
            log.error("🚨 [KakaoAuthService] 액세스 토큰을 가져오지 못했습니다. 카카오 로그인 실패");
            return Collections.singletonMap("success", false);
        }

        Map<String, Object> kakaoUser = getKakaoUserInfo(accessToken);

        if (kakaoUser == null || !kakaoUser.containsKey("id")) {
            log.error("🚨 [KakaoAuthService] 카카오 사용자 정보를 가져오지 못했습니다.");
            return Collections.singletonMap("success", false);
        }

        String kakaoId = kakaoUser.get("id").toString();
        String nickname = (String) kakaoUser.get("nickname");

        log.info("✅ [KakaoAuthService] 카카오 ID 가져옴: {}", kakaoId);

        // ✅ `Member` 객체로 변환
        Member member = memberService.findOrCreateMember(kakaoId, nickname, "kakao");

        if (member == null) {
            log.error("🚨 `findOrCreateMember`가 null을 반환했습니다! KakaoId={}, nickname={}", kakaoId, nickname);
            return Collections.singletonMap("success", false);
        }

        log.info("✅ [KakaoAuthService] 로그인한 사용자: memberId={}, nickname={}", member.getMemberId(), member.getMemberName());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("loginUser", member); // ✅ `Member` 객체 반환

        return response;
    }



    private String getKakaoAccessToken(String code) {
        String tokenUrl = "https://kauth.kakao.com/oauth/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        String requestBody = "grant_type=authorization_code"
                + "&client_id=" + "6ee1731553a983102257108c54fe99bc"
                + "&redirect_uri=http://localhost:3000/login"
                + "&code=" + code;

        HttpEntity<String> request = new HttpEntity<>(requestBody, headers);

        try {
            ResponseEntity<Map> response = restTemplate.exchange(tokenUrl, HttpMethod.POST, request, Map.class);

            if (response.getBody() == null || !response.getBody().containsKey("access_token")) {
                log.error("🚨 [KakaoAuthService] 액세스 토큰을 받아오지 못했습니다. 응답: {}", response.getBody());
                return null;
            }

            String accessToken = (String) response.getBody().get("access_token");
            log.info("✅ [KakaoAuthService] 액세스 토큰 가져옴: {}", accessToken);
            return accessToken;

        } catch (Exception e) {
            log.error("🚨 [KakaoAuthService] 액세스 토큰 요청 중 오류 발생: {}", e.getMessage());
            return null;
        }
    }


    private Map<String, Object> getKakaoUserInfo(String accessToken) {
        String userInfoUrl = "https://kapi.kakao.com/v2/user/me";

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + accessToken);

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(userInfoUrl, HttpMethod.GET, request, Map.class);

        Map<String, Object> userInfo = response.getBody();
        if (userInfo != null) {
            Object kakaoIdObj = userInfo.get("id");

            if (kakaoIdObj == null) {
                System.out.println("🚨 [KakaoAuthService] 카카오 ID가 `null`입니다! 응답 확인 필요: " + userInfo);
                return null;
            } else {
                System.out.println("✅ [KakaoAuthService] 카카오 ID 가져옴: " + kakaoIdObj);
            }

            String kakaoId = kakaoIdObj.toString();
            Map<String, Object> kakaoAccount = (Map<String, Object>) userInfo.get("kakao_account");
            Map<String, Object> profile = (Map<String, Object>) kakaoAccount.get("profile");

            String nickname = profile.get("nickname") != null ? profile.get("nickname").toString() : "카카오 사용자";

            // ✅ userInfo 최상위에 저장
            userInfo.put("kakaoId", kakaoId);
            userInfo.put("nickname", nickname);

            System.out.println("🔎 [KakaoAuthService] 카카오 API 응답: " + userInfo);

            // 🔥 최종 로그 확인
            System.out.println("🔎 [KakaoAuthService] 최종적으로 `userInfo`에 저장된 데이터: " + userInfo);
        }

        return userInfo;
    }


}
