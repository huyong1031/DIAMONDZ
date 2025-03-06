package com.himedia.spserver.security.filter;

import com.google.gson.Gson;
import com.himedia.spserver.dto.MemberDTO;
import com.himedia.spserver.security.util.JWTUtil;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public class JWTCheckFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeaderStr = request.getHeader("Authorization");
        System.out.println("=====================================================authHeaderStr:" + authHeaderStr);

        // 이메일 인증 요청은 JWT 토큰 없이도 처리하도록 설정
        String path = request.getRequestURI();
        if (path.startsWith("/api/member/auth/send-email") || path.startsWith("/api/member/auth/verify-email")) {
            // 이메일 인증 요청인 경우, JWT 검증을 생략하고 바로 필터 통과
            filterChain.doFilter(request, response);
            return;
        }

        // JWT 토큰이 있을 경우
        try {
            if (authHeaderStr != null && authHeaderStr.startsWith("Bearer ")) {
                String accessToken = authHeaderStr.substring(7); // "Bearer " 뒤에 있는 실제 토큰 부분만 추출
                Map<String, Object> claims = JWTUtil.validateToken(accessToken); // JWT 토큰 검증

                // JWT 토큰에서 정보 추출
                String memberId = (String) claims.get("memberId");
                String memberPwd = (String) claims.get("memberPwd");
                String memberName = (String) claims.get("memberName");
//                String memberBirthdate = (String) claims.get("memberBirthdate");
                String memberPhone = (String) claims.get("memberPhone");
                String memberEmail = (String) claims.get("memberEmail");
                String zipNum = (String) claims.get("zipNum");
                String memberAddress1 = (String) claims.get("memberAddress1");
                String memberAddress2 = (String) claims.get("memberAddress2");
                String memberAddress3 = (String) claims.get("memberAddress3");
                BigDecimal memberPoints = BigDecimal.valueOf((Double) claims.get("memberPoints"));
                String provider = (String) claims.get("provider");
                boolean birthdateRewarded = (boolean) claims.get("birthdateRewarded");
                List<String> roleNames = (List<String>) claims.get("roleNames");

                // MemberDTO 생성
                MemberDTO memberDTO = new MemberDTO(memberId, memberPwd, memberName, memberPhone, memberEmail,
                        memberAddress1, memberAddress2, memberAddress3, zipNum, memberPoints,
                        provider, birthdateRewarded, roleNames);

                // 인증 처리
                UsernamePasswordAuthenticationToken authenticationToken
                        = new UsernamePasswordAuthenticationToken(memberDTO, memberPwd, memberDTO.getAuthorities());
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);

                // 필터 체인 진행
                filterChain.doFilter(request, response);

            } else {
                // JWT 토큰이 없을 경우 오류 처리 (토큰이 필요 없는 경우 이메일 인증 제외)
                throw new ServletException("JWT 토큰이 유효하지 않거나 존재하지 않음");
            }
        } catch (Exception e) {
            System.out.println("e.getMessage():" + e.getMessage());
            // 예외 처리 (JWT 토큰 오류)
            System.out.println("JWT Check Error..............");
            System.out.println(e.getMessage());
            Gson gson = new Gson();
            String msg = gson.toJson(Map.of("error", "ERROR_ACCESS_TOKEN"));
            response.setContentType("application/json");
            PrintWriter printWriter = response.getWriter();
            printWriter.println(msg);
            printWriter.close();
        }
    }



    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String path = request.getRequestURI();
        System.out.println("check uri :" + path);

        // 로그인 및 회원가입 관련 요청은 필터링하지 않음

        if(request.getMethod().equals("OPTIONS"))
            return true;

        if (path.startsWith("/member/loginLocal"))
            return true;

        if (path.startsWith("/member/register"))
            return true;

        if (path.startsWith("/member/idCheck"))
            return true;

        if (path.startsWith("/member/kakaoLogin"))
            return true;

        if (path.startsWith("/member/verifyKakaoUser"))
            return true;

        if(path.startsWith("/member/refresh"))
            return true;

        // 상품 관련 요청들도 인증 없이 접근할 수 있도록 설정
        if (path.startsWith("/product/bestPro"))
            return true;

        if (path.startsWith("/product/newPro"))
            return true;

        if (path.startsWith("/product/selectPro"))
            return true;

        if (path.startsWith("/product/categoryList"))
            return true;

        if (path.startsWith("/product/search"))
            return true;

        if (path.startsWith("/product/getProduct"))
            return true;

        if (path.startsWith("/product/search"))
            return true;

        // 이미지 관련 요청들

        if(path.startsWith("/customer_images/"))
            return true;

        if(path.startsWith("/product_hover/"))
            return true;

        if(path.startsWith("/product_images/"))
            return true;

        if(path.startsWith("/product_infoimages/"))
            return true;

        if(path.startsWith("/favicon.ico"))
            return true;

        // 주문 관련 요청도 인증 없이 접근할 수 있도록 설정
//        if (path.startsWith("/api/orders/history"))
//            return true;

//        if (path.startsWith("/api/orders/createOrders"))
//            return true;

        // 리뷰 관련 요청도 인증 없이 접근할 수 있도록 설정
        if (path.startsWith("/review/getReview"))
            return true;

        // 좋아요 관련 요청도 인증 없이 접근할 수 있도록 설정
//        if (path.startsWith("/post/addLike"))
//            return true;
//
//        if (path.startsWith("/post/removeLike"))
//            return true;
//
//        if (path.startsWith("/post/getUserLikes"))
//            return true;

        // 이메일 관련 요청도 인증 없이 접근

        if (path.startsWith("/seller/sendMail"))
            return true;

        if (path.startsWith("/seller/codecheck"))
            return true;

        if (path.startsWith("/member/auth/send-email"))
            return true;

        if (path.startsWith("/member/auth/verify-email"))
            return true;

        // 카트 관련 요청도 인증 없이 접근

//        if (path.startsWith("/cart/insertCart"))
//            return true;
//
//        if (path.startsWith("/cart/getCartList"))
//            return true;

        // 포인트 관련 요청도 인증 없이 접근

//        if (path.startsWith("/points/earn"))
//            return true;
//
//        if (path.startsWith("/points/use"))
//            return true;
//
//        if (path.startsWith("/points/history/{memberId}"))
//            return true;

        if (path.startsWith("/customer/allQnaList"))
            return true;

        if (path.startsWith("/customer/getQna"))
            return true;
        if (path.startsWith("/customer/confirmPass"))
            return true;













        return false;
    }
}
