package com.himedia.spserver.controller;

import com.himedia.spserver.dto.MemberDTO;
import com.himedia.spserver.entity.Member;
import com.himedia.spserver.entity.PointHistory;
import com.himedia.spserver.repository.MemberRepository;

import com.himedia.spserver.repository.PointHistoryRepository;
import com.himedia.spserver.repository.ReviewRepository;
import com.himedia.spserver.security.util.CustomJWTException;
import com.himedia.spserver.security.util.JWTUtil;
import com.himedia.spserver.service.KakaoAuthService;
import com.himedia.spserver.service.MemberService;
import com.himedia.spserver.service.PointService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@RestController
@RequestMapping("/member")
public class MemberController {

    private final MemberService memberService;


    @Autowired
    private PointService pointService;

    private final MemberRepository memberRepository;
    private final ReviewRepository reviewRepository;
    private final KakaoAuthService kakaoAuthService;
    private final PointHistoryRepository pointHistoryRepository;

    @Autowired
    public MemberController(MemberService memberService,
                            MemberRepository memberRepository,
                            ReviewRepository reviewRepository,
                            KakaoAuthService kakaoAuthService,
                            PointHistoryRepository pointHistoryRepository) {
        this.memberService = memberService;
        this.memberRepository = memberRepository;
        this.reviewRepository = reviewRepository;
        this.kakaoAuthService = kakaoAuthService;
        this.pointHistoryRepository = pointHistoryRepository;
    }




    @PostMapping("/loginLocal")
    public ResponseEntity<?> loginLocal(@RequestParam("memberId") String memberId,
                                        @RequestParam("memberPwd") String memberPwd,
                                        HttpServletRequest request,
                                        HttpServletResponse response,
                                        HttpSession session) {
        HashMap<String, Object> result = new HashMap<>();
        Member member = memberService.getMember(memberId);

        if (member == null || !member.getMemberPwd().equals(memberPwd)) {
            result.put("msg", "아이디 또는 패스워드를 확인하세요");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(result);
        }

        System.out.println("✅ 로그인 요청: " + memberId);
        System.out.println("✅ 로그인 성공: " + member.getMemberId());

        session.invalidate(); // 기존 세션 무효화
        session = request.getSession(true); // 새로운 세션 생성
        session.setAttribute("loginUser", member); // ✅ 세션에 Member 객체 저장
        session.setAttribute("userPoints", member.getMemberPoints()); // ✅ 추가(포인트 정보 가져오기)

        // ✅ 콘솔 로그 추가 (points 값 확인)
        System.out.println("✅ 로그인한 회원 정보: " + member.getMemberId() + " | 포인트: " + member.getMemberPoints());

        Object sessionUser = session.getAttribute("loginUser");
        System.out.println("🔎 현재 세션 loginUser: " + sessionUser); // 로그 확인

        // ✅ JSON 객체로 변환하여 응답
        HashMap<String, Object> loginUser = new HashMap<>();
        loginUser.put("memberId", member.getMemberId());
        loginUser.put("memberName", member.getMemberName());
        loginUser.put("memberEmail", member.getMemberEmail());
        loginUser.put("memberPhone", member.getMemberPhone());
        loginUser.put("zipNum", member.getZipNum());
        loginUser.put("memberAddress1", member.getMemberAddress1());
        loginUser.put("memberAddress2", member.getMemberAddress2());
        loginUser.put("memberAddress3", member.getMemberAddress3());
        loginUser.put("points", member.getMemberPoints()); // ✅ BigDecimal → int 변환
        loginUser.put("inDate", member.getInDate());

        result.put("loginUser", loginUser);

        return ResponseEntity.ok().body(result);
    }

    @GetMapping("/userinfo")
    public ResponseEntity<?> getUserInfo(HttpSession session) {
        MemberDTO memberDTO= (MemberDTO) session.getAttribute("loginUser");
        Member user = memberRepository.findByMemberId(memberDTO.getUsername()).orElseThrow();


        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("msg", "로그인이 필요합니다."));
        }
        // ✅ 사용자의 누적 포인트 조회
        BigDecimal totalPoints = user.getMemberPoints(); // ✅ DB에 저장된 `points` 값

        // ✅ 응답 구조를 단순화하여 `response.data.memberId`로 바로 접근 가능
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("memberId", user.getMemberId());
        userInfo.put("memberName", user.getMemberName());
        userInfo.put("memberEmail", user.getMemberEmail());
        userInfo.put("memberPhone", user.getMemberPhone());
        userInfo.put("zipNum", user.getZipNum() != null ? user.getZipNum() : "");
        userInfo.put("memberAddress1", user.getMemberAddress1() != null ? user.getMemberAddress1() : "");
        userInfo.put("memberAddress2", user.getMemberAddress2() != null ? user.getMemberAddress2() : "");
        userInfo.put("memberBirthdate", user.getMemberBirthdate() != null ? user.getMemberBirthdate() : ""); // ✅ 생년월일 추가
        userInfo.put("points", totalPoints); // ✅ `points` 값 추가
        System.out.println("✅ 반환할 사용자 정보: " + userInfo);

        return ResponseEntity.ok(userInfo);
    }



    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {
        // ✅ 세션 무효화
        request.getSession().invalidate();

        // ✅ 쿠키 삭제
        Cookie cookie = new Cookie("JSESSIONID", null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        response.addCookie(cookie);

        System.out.println("✅ 로그아웃 처리 완료");
        return ResponseEntity.ok().body("{\"success\": true, \"message\": \"로그아웃 성공\"}");
    }


    @PostMapping("/idCheck") // ShoesShop 방식
    public String checkIdAvailability(@RequestBody Map<String, String> request) {
        String memberId = request.get("userid");
        boolean exists = memberRepository.existsById(memberId); // DB 조회

        return exists ? "unusable" : "usable";
    }

    @PostMapping("/register")
    @Transactional
    public ResponseEntity<Map<String, Object>> registerMember(@RequestBody Member member) {
        Map<String, Object> response = new HashMap<>();

        try {
            if (memberRepository.existsById(member.getMemberId())) {
                response.put("msg", "이미 사용 중인 아이디입니다.");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
            }

            // ✅ 회원 정보 저장
            memberService.insertMember(member);
            response.put("msg", "success");
            boolean isPointsGiven = false;

        // 멤버 저장

            // ✅ 회원가입 시 생일이 입력되었다면 5000P 지급
            if (member.getMemberBirthdate() != null && !member.isBirthdateRewarded()) {
                BigDecimal birthdatePoints = BigDecimal.valueOf(5000);

                // ✅ 포인트 지급
                member.setMemberPoints(member.getMemberPoints().add(birthdatePoints));
                member.setBirthdateRewarded(true);
                memberRepository.save(member);
                isPointsGiven = true;

                // ✅ 포인트 내역 저장
                PointHistory history = new PointHistory();
                history.setMember(member);
                history.setPoints(birthdatePoints);
                history.setType(PointHistory.PointType.EARN);
                history.setDescription("회원가입 생일 입력 보너스");
                history.setCreatedAt(LocalDateTime.now());

                pointHistoryRepository.save(history);
            }

            response.put("msg", "회원가입이 완료되었습니다!");
            response.put("birthdateRewarded", member.isBirthdateRewarded());
            response.put("isPointsGiven", isPointsGiven);

            if (isPointsGiven) {
                response.put("bonusPoints", 5000);
                response.put("message", "생일 입력으로 5000P가 지급되었습니다.");
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("msg", "회원가입 중 오류가 발생했습니다.");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }


    @PostMapping("/verify-password")
    public ResponseEntity<?> verifyPassword(@RequestBody Map<String, String> request, @RequestHeader("Authorization") String memberId) {
        String inputPassword = request.get("password");

        System.out.println("🔎 요청된 비밀번호 확인: " + inputPassword);
        System.out.println("🔐 전달된 memberId: " + memberId);

        // 🔹 `memberId`로 사용자 정보 조회
        Member loginUser = memberService.getMember(memberId);

        if (loginUser == null) {
            System.out.println("🚨 로그인 정보 없음! memberId가 잘못되었을 가능성.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("msg", "로그인이 필요합니다."));
        }

        System.out.println("✅ 로그인된 사용자: " + loginUser.getMemberId());

        try {
            boolean isValid = memberService.checkPassword(memberId, inputPassword);

            if (isValid) {
                System.out.println("🔓 비밀번호 일치!");
                return ResponseEntity.ok().body(Collections.singletonMap("msg", "비밀번호 확인 성공"));
            } else {
                System.out.println("❌ 비밀번호 불일치!");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("msg", "비밀번호가 일치하지 않습니다."));
            }
        } catch (Exception e) {
            System.out.println("🚨 서버 오류 발생: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("msg", "서버 오류가 발생했습니다."));
        }
    }


    @PostMapping("/update")
    public ResponseEntity<?> updateMember(@RequestBody Map<String, String> request, HttpSession session) {

        MemberDTO memberDTO= (MemberDTO) session.getAttribute("loginUser");
        System.out.println("===========================memberDTO: " + memberDTO);
        Member loginUser = memberRepository.findByMemberId(memberDTO.getUsername()).orElseThrow();
        System.out.println(loginUser);


        if (loginUser == null) {
            System.out.println("🚨 로그인 정보 없음: 세션이 비어 있음");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(Collections.singletonMap("msg", "로그인이 필요합니다."));
        }

        String requestMemberId = request.get("memberId");
        if (!loginUser.getMemberId().equals(requestMemberId)) {
            System.out.println("🚨 본인 인증 실패: 로그인된 사용자와 요청된 사용자 ID 불일치");
            System.out.println("🔹 로그인된 ID: " + loginUser.getMemberId() + ", 요청된 ID: " + requestMemberId);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(Collections.singletonMap("msg", "본인 인증 실패"));
        }

        System.out.println("✅ 본인 인증 성공: " + requestMemberId + " 정보 업데이트 중...");

        // ✅ 비밀번호 변경 여부 확인
        String newPassword = request.get("newPwd");
        String confirmPassword = request.get("confirmPwd");

        if (newPassword != null && !newPassword.isEmpty()) {
            if (!newPassword.equals(confirmPassword)) {
                System.out.println("❌ 비밀번호 불일치: 새 비밀번호가 일치하지 않음");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("msg", "새 비밀번호가 일치하지 않습니다."));
            }
            BCryptPasswordEncoder encoder=new BCryptPasswordEncoder();
            loginUser.setMemberPwd(encoder.encode(newPassword));
        }

        // ✅ 회원정보 업데이트
        loginUser.setMemberName(request.get("memberName"));
        loginUser.setMemberPhone(request.get("memberPhone"));
        loginUser.setMemberAddress1(request.get("memberAddress1"));
        loginUser.setMemberAddress2(request.get("memberAddress2"));
        loginUser.setZipNum(request.get("zipNum"));

        // ✅ 생일 값 처리 (null 또는 빈 문자열 체크)
        if (request.containsKey("memberBirthdate")) {
            String birthdate = request.get("memberBirthdate");
            if (birthdate == null || birthdate.trim().isEmpty()) {
                loginUser.setMemberBirthdate(null); // ✅ 생일을 NULL로 저장
            } else {
                try {
                    loginUser.setMemberBirthdate(LocalDate.parse(birthdate)); // ✅ 올바른 날짜 변환
                } catch (Exception e) {
                    System.out.println("🚨 생일 변환 오류: " + birthdate);
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Collections.singletonMap("msg", "잘못된 날짜 형식입니다."));
                }
            }
        }

        memberService.updateMember(loginUser);
        System.out.println("✅ 회원정보 업데이트 완료: " + requestMemberId);

        return ResponseEntity.ok(Collections.singletonMap("msg", "회원정보 수정 완료"));
    }


    @GetMapping("/{memberId}/profile")
    public ResponseEntity<?> getUserProfile(@PathVariable String memberId) {
        // 회원 정보 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 🔥 PointHistory에서 실제 포인트 총합 계산
        BigDecimal totalPoints = pointHistoryRepository.getTotalPoints(memberId);

        // JSON 응답
        Map<String, Object> response = new HashMap<>();
        response.put("memberId", member.getMemberId());
        response.put("memberName", member.getMemberName());
        response.put("points", totalPoints); // ✅ `PointHistory`에서 계산된 값으로 변경

        return ResponseEntity.ok(response);
    }


    @PostMapping("/update-birthdate")
    public ResponseEntity<Map<String, Object>> updateBirthdate(@RequestBody Map<String, Object> request) {
        try {
            String memberId = (String) request.get("memberId");
            Object birthdateObj = request.get("birthdate");

            Optional<Member> optionalMember = memberRepository.findById(memberId);
            if (!optionalMember.isPresent()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(Map.of("success", false, "message", "회원 정보를 찾을 수 없습니다."));
            }

            Member member = optionalMember.get();

            // 🔥 NULL 값 처리 (문자열로 들어오는 경우 대비)
            if (birthdateObj == null || birthdateObj.toString().trim().isEmpty() || "null".equalsIgnoreCase(birthdateObj.toString())) {
                member.setMemberBirthdate(null); // ✅ 생일을 NULL로 업데이트
                memberRepository.save(member);
                return ResponseEntity.ok(Map.of("success", true, "message", "생일이 삭제되었습니다."));
            }

            // 🎂 생일이 이미 입력되어 있고, 포인트가 지급된 경우 다시 지급 방지
            if (member.isBirthdateRewarded()) {
                return ResponseEntity.ok(Map.of("success", false, "message", "이미 생일이 입력되어 포인트가 지급되었습니다."));
            }

            // 🎂 생일 저장 및 5000P 지급
            member.setMemberBirthdate(LocalDate.parse(birthdateObj.toString()));
            member.setBirthdateRewarded(true); // 포인트 지급 여부 업데이트
            member.setMemberPoints(member.getMemberPoints().add(BigDecimal.valueOf(5000)));

            memberRepository.save(member);

            // 📌 포인트 적립 내역 저장
            PointHistory pointHistory = new PointHistory();
            pointHistory.setMember(member);
            pointHistory.setPoints(BigDecimal.valueOf(5000));
            pointHistory.setType(PointHistory.PointType.EARN);
            pointHistory.setDescription("생일 입력 보너스");
            pointHistoryRepository.save(pointHistory);

            return ResponseEntity.ok(Map.of("success", true, "message", "생일이 입력되었으며 5000P가 지급되었습니다!"));

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("success", false, "message", "서버 오류 발생", "error", e.getMessage()));
        }
    }


    @GetMapping("/{memberId}/reviews/count")
    public ResponseEntity<Map<String, Object>> getReviewCount(@PathVariable String memberId) {
        try {
            log.info("📌 요청된 memberId: {}", memberId); // ✅ 기존 System.out.println() → log.info()로 변경
            int reviewCount = reviewRepository.countReviewsByMemberId(memberId);
            log.info("📌 조회된 리뷰 개수: {}", reviewCount); // ✅ 로그 기록 보장

            return ResponseEntity.ok(Collections.singletonMap("count", reviewCount));
        } catch (Exception e) {
            log.error("🚨 리뷰 개수 조회 중 오류 발생:", e); // ✅ 예외 로그도 log.error()로 출력

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "리뷰 개수 조회 실패");
            errorResponse.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/{memberId}/points")
    public ResponseEntity<Map<String, Object>> getPoints(@PathVariable String memberId) {
        BigDecimal totalPoints = pointHistoryRepository.getTotalPoints(memberId);
        return ResponseEntity.ok(Map.of("success", true, "points", totalPoints));
    }


    @GetMapping("/{memberId}/points/history")
    public ResponseEntity<Page<PointHistory>> getPointHistory(
            @PathVariable String memberId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);  // 🔹 요청된 페이지 및 크기 적용
        Page<PointHistory> historyPage = pointService.getPointHistory(memberId, pageable);

        return ResponseEntity.ok(historyPage);
    }




    @DeleteMapping("/withdraw")
    public ResponseEntity<String> withdrawMember(@RequestParam String memberId, @RequestParam(required = false) String password, HttpSession session) {
        System.out.println("🔹 회원 탈퇴 API 호출됨 | memberId: " + memberId);

        Member loginUser = (Member) session.getAttribute("loginUser");

        if (loginUser == null || !loginUser.getMemberId().equals(memberId)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("잘못된 접근입니다.");
        }

        // ✅ 카카오 로그인 사용자는 비밀번호 없이 탈퇴 가능
        if ("kakao".equals(loginUser.getProvider())) {
            boolean isDeleted = memberService.withdrawMember(memberId, null);
            if (isDeleted) {
                session.invalidate();  // ✅ 세션 무효화
                return ResponseEntity.ok("회원 탈퇴 완료");
            } else {
                return ResponseEntity.badRequest().body("회원 탈퇴 실패");
            }
        }

        // ✅ 일반 회원은 비밀번호 검증 후 탈퇴
        if (password == null || password.isEmpty()) {
            return ResponseEntity.badRequest().body("비밀번호가 필요합니다.");
        }

        System.out.println("🔹 회원 탈퇴 중 리뷰 삭제 시도: " + memberId);
        reviewRepository.deleteByMemberId(memberId);
        System.out.println("✅ 리뷰 삭제 완료");


        boolean isDeleted = memberService.withdrawMember(memberId, password);
        if (isDeleted) {
            session.invalidate();  // ✅ 세션 무효화
            return ResponseEntity.ok("회원 탈퇴 완료");
        } else {
            return ResponseEntity.badRequest().body("비밀번호가 일치하지 않습니다.");
        }
    }
    @GetMapping("/kakaoLogin")
    public ResponseEntity<?> kakaoLogin(@RequestParam("code") String code, HttpSession session) {
        log.info("📌 카카오 로그인 요청됨, 코드: {}", code);

        Map<String, Object> kakaoUser = kakaoAuthService.kakaoLogin(code);

        if (!(Boolean) kakaoUser.get("success")) {
            log.error("🚨 카카오 로그인 실패, 응답: {}", kakaoUser);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("msg", "카카오 로그인 실패"));
        }

        // ✅ `Member` 객체로 변환
        Member member = (Member) kakaoUser.get("loginUser");

        if (member == null) {
            log.error("🚨 `loginUser` 정보가 없음! 응답 확인 필요: {}", kakaoUser);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("msg", "잘못된 카카오 사용자 정보"));
        }

        // ✅ 세션에 `Member` 객체 저장
        session.setAttribute("loginUser", member);
        log.info("✅ 로그인 완료: memberId={}, nickname={}, memberPwd={}", member.getMemberId(), member.getMemberName(),member.getMemberPwd());

        return ResponseEntity.ok(Collections.singletonMap("loginUser", member));
    }

    @GetMapping("/verifyKakaoUser")
    public ResponseEntity<?> verifyKakaoUser(HttpSession session, @RequestParam("memberId") String memberId) {
        Member loginUser = (Member) session.getAttribute("loginUser");

        if (loginUser == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인 정보 없음");
        }

        if (!loginUser.getMemberId().equals(memberId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("본인 인증 실패");
        }

        return ResponseEntity.ok("인증 성공");
    }

    @GetMapping("/refresh")
    public HashMap<String, String> refresh(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam("refreshToken") String refreshToken
    ) throws CustomJWTException {
        HashMap<String, String> result = new HashMap<>();

        if (refreshToken == null) throw new CustomJWTException("NULL_REFRESH");
        if (authHeader == null || authHeader.length() < 7) throw new CustomJWTException("INVALID_HEADER");

        String accessToken = authHeader.substring(7);

        // 토큰 만료 확인
        boolean expiredResult = checkExpiredToken(accessToken);

        if (expiredResult) {
            System.out.println("토큰 유효기간 만료 전... 계속 사용");
            result.put("accessToken", accessToken);
            result.put("refreshToken", refreshToken);
        } else {
            System.out.println("토큰 교체");
            Map<String, Object> claims = null;

            try {
                // refreshToken 검증 및 사용자 정보 추출
                claims = JWTUtil.validateToken(refreshToken);
            } catch (Exception e) {
                throw new CustomJWTException("INVALID_REFRESH_TOKEN");
            }

            // 새로운 accessToken 생성
            String newAccessToken = JWTUtil.generateToken(claims, 1);

            // 새로운 refreshToken 생성 여부 확인
            String newRefreshToken = "";
            if (checkTime((Integer) claims.get("exp"))) {
                newRefreshToken = JWTUtil.generateToken(claims, 60 * 24);
            } else {
                newRefreshToken = refreshToken;
            }

            result.put("accessToken", newAccessToken);
            result.put("refreshToken", newRefreshToken);
        }
        return result;
    }

    private boolean checkTime(Integer exp) {
        java.util.Date expDate = new java.util.Date( (long)exp * (1000 ));//밀리초로 변환
        long gap = expDate.getTime() - System.currentTimeMillis();//현재 시간과의 차이 계산
        long leftMin = gap / (1000 * 60); //분단위 변환
        return leftMin < 60;  // 한시간 미만으로 남았으면  true 그렇지 않으면 false 가 리턴
    }

    private boolean checkExpiredToken(String accessToken) {
        try {
            JWTUtil.validateToken( accessToken );
        } catch (CustomJWTException e) {
            if( e.getMessage().equals("Expired") ) return false;
        }
        return true;

    }


}



