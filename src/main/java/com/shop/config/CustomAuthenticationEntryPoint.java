package com.shop.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException)
            throws IOException, ServletException {

        final String loginPageUrl = "/members/login"; // 리다이렉트할 로그인 페이지의 URL
        final String message = "로그인이 필요합니다"; // 로그인 필요시 메세지

        //request.getSession().setAttribute("loginMessage", message); // 세션에 메시지 저장
        //response.sendRedirect(loginPageUrl); // 로그인 페이지 url 지정
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
    }
}

