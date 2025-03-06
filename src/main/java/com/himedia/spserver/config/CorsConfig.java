// CorsConfig.java (수정)
package com.himedia.spserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import java.util.List;


@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        config.setAllowCredentials(true);
        config.setAllowedOriginPatterns(List.of("*"));  // ✅ 모든 도메인 허용
//        config.setAllowedOrigins(List.of("http://localhost:3000", "http://localhost:3001")); // React 프론트엔드 주소, List.of 사용
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS")); // List.of 사용
        config.setAllowedHeaders(List.of("*")); // List.of 사용
        config.setExposedHeaders(List.of("Authorization", "Content-Type", "Set-Cookie"));
        source.registerCorsConfiguration("/**", config); // 모든 경로에 적용
        return new CorsFilter(source);
    }
}