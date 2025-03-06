package com.himedia.spserver.security;

import com.himedia.spserver.security.filter.JWTCheckFilter;
import com.himedia.spserver.security.handler.APILoginFailHandler;
import com.himedia.spserver.security.handler.APILoginSuccessHandler;
import com.himedia.spserver.security.handler.CustomAccessDeniedHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
public class CustomSecurityConfig {

    // security 가 시작되면 가장 먼저 자동으로 실행되는 메서드
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("Security Filter Chain - Security Config Start------------------------------------------------");
        //--------------------------------------------------------------
        http.cors(
                httpSecurityCorsConfigurer -> {
                    httpSecurityCorsConfigurer.configurationSource(  corsConfigurationSource()  );
                }
        );
        //--------------------------------------------------------------
        http.csrf(config -> config.disable());
        //--------------------------------------------------------------
        http.sessionManagement(
                sessionConfig->sessionConfig.sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
        );
        //--------------------------------------------------------------
        http.formLogin(
                config ->{
                    config.loginPage("/member/loginLocal");
                    // -> security / service / CustomUserDetailServicee 안의
                    // loadUserByUsername 메서드로 이동
                    config.successHandler( new APILoginSuccessHandler() );
                    config.failureHandler( new APILoginFailHandler() );
                }
        );
        //--------------------------------------------------------------
        http.addFilterBefore(new JWTCheckFilter(), UsernamePasswordAuthenticationFilter.class);
        //--------------------------------------------------------------
        http.exceptionHandling(config -> {
            config.accessDeniedHandler(new CustomAccessDeniedHandler());
        });
        //--------------------------------------------------------------
        return http.build();
    }

    // security 가 전송되어져오는 패스워드를 암호화하기 위해 사용할 메서드
    @Bean
    public PasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    // 위쪽에 있는 securityFilterChain 메서드가 cors 설정때 사용할 예정
    @Bean
    public CorsConfigurationSource corsConfigurationSource(){
        CorsConfiguration configuration = new CorsConfiguration();
        // 아이피
        configuration.setAllowedOriginPatterns( Arrays.asList("*") );
        // 메서드
        configuration.setAllowedMethods( Arrays.asList("HEAD", "GET", "POST", "PUT", "DELETE") );
        // 헤더
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type"));
        // 전송해줄 데이터의 JSON 처리
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

}
