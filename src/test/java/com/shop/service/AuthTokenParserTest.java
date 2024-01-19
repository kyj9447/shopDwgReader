package com.shop.service;

import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import lombok.extern.slf4j.Slf4j;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import static com.shop.service.AuthTokenParser.isAdmin;
//import static com.shop.service.AuthTokenParser.isAdmin2;


@SpringBootTest
@Slf4j
public class AuthTokenParserTest {

    // 테스트를 위한 가짜 Principal 객체 생성
    private static Principal createTestPrincipal(boolean isAdmin) {

        // 사용자 이름
        String username = "test";

        String role;
        // "ROLE_ADMIN" 권한을 가진 SimpleGrantedAuthority 생성
        if(isAdmin){
            role = "ROLE_ADMIN";
        }
        else {
            role = "ROLE_USER";
        }
        GrantedAuthority adminAuthority = new SimpleGrantedAuthority(role);

        // 권한 목록 생성 및 "ROLE_ADMIN" 권한 추가
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(adminAuthority);

        // UsernamePasswordAuthenticationToken 객체 생성 및 권한 목록 설정
        // 원하는 Principal 객체를 생성하고 반환
        return new UsernamePasswordAuthenticationToken(username, "password123", authorities);
    }

    @Test
    @DisplayName("토큰테스트 true")
    public void isAdminTest() {
        // 테스트를 위한 Principal 객체 생성
        Principal principal = createTestPrincipal(true);

        for (int i = 0 ; i<10 ; i++ ) {
            // isAdmin 메서드 실행 시간 측정
            long startTime = System.nanoTime();
            boolean isAdminResult = isAdmin(principal);
            long endTime = System.nanoTime();
            long duration = endTime - startTime;

            // 결과 출력
            log.info("isAdmin 실행 시간 (나노초): " + duration);
        }

        for (int i = 0 ; i<10 ; i++ ) {
            // isAdmin 메서드 실행 시간 재측정
            long startTime = System.nanoTime();
            boolean isAdminResult = isAdmin(principal);
            long endTime = System.nanoTime();
            long duration = endTime - startTime;

            // 결과 출력
            log.info("isAdmin 실행 시간 (나노초): " + duration);
        }
    }

    @Test
    @DisplayName("토큰테스트 false")
    public void notAdminTest() {
        // 테스트를 위한 Principal 객체 생성
        Principal principal = createTestPrincipal(false);

        for (int i = 0 ; i<10 ; i++ ) {
            // isAdmin 메서드 실행 시간 측정
            long startTime = System.nanoTime();
            boolean isAdminResult = isAdmin(principal);
            long endTime = System.nanoTime();
            long duration = endTime - startTime;

            // 결과 출력
            log.info("isAdmin 실행 시간 (나노초): " + duration);
        }

        for (int i = 0 ; i<10 ; i++ ) {
            // isAdmin 메서드 실행 시간 재측정
            long startTime = System.nanoTime();
            boolean isAdminResult = isAdmin(principal);
            long endTime = System.nanoTime();
            long duration = endTime - startTime;

            // 결과 출력
            log.info("isAdmin 실행 시간 (나노초): " + duration);
        }
    }
}