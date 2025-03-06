package com.himedia.spserver.security.util;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.InvalidClaimException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.sql.Date;
import java.time.ZonedDateTime;
import java.util.Map;

public class JWTUtil {
    private static String key = "1234567890123456789012345678901234567890";

    public static String generateToken(Map<String, Object> claims, int i) {
        SecretKey key = null;
        try {
            key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        String jwtStr = Jwts.builder()
                .setHeader(Map.of("typ", "JWT"))
                .setClaims(claims)
                .setIssuedAt(Date.from(ZonedDateTime.now().toInstant()))  // 현재시간
                .setExpiration(Date.from(ZonedDateTime.now().plusMinutes(i).toInstant()))  // 유효시간
                .signWith(key)
                .compact();
        return jwtStr;
    }

    public static Map<String, Object> validateToken(String accessToken) throws CustomJWTException {
        Map<String, Object> claim = null;
        SecretKey key = null;
        try {
            key = Keys.hmacShaKeyFor(JWTUtil.key.getBytes("UTF-8"));
            claim = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(accessToken)
                    .getBody();
        } catch(ExpiredJwtException expiredJwtException){
            System.out.println("JWT 토큰 만료: " + expiredJwtException.getMessage()); // 로그 추가
            throw new CustomJWTException("Expired");
        } catch(InvalidClaimException invalidClaimException){
            System.out.println("잘못된 JWT 클레임: " + invalidClaimException.getMessage()); // 로그 추가
            throw new CustomJWTException("Invalid");
        } catch(JwtException jwtException){
            System.out.println("JWT 오류: " + jwtException.getMessage()); // 로그 추가
            throw new CustomJWTException("JWTError");
        } catch(Exception e){
            System.out.println("토큰 오류: " + e.getMessage()); // 로그 추가
            throw new CustomJWTException("Error");
        }
        return claim;
    }

}
