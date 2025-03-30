package com.shop.config;

import com.shop.service.AuthTokenParser;
import com.shop.service.CustomUserDetailsService;
import com.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
// WebSecurityConfigurerAdapter 를 상속받는 클래스에
// @EnableWebSecurity를 선언하면
// SpringSecurityfilterChain이 자동 포함 메소드를 오버라이딩 가능
@Slf4j

public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    MemberService memberService;

    @Autowired
    CustomUserDetailsService customUserDetailsService;

    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.formLogin()
                .loginPage("/members/login")
                .defaultSuccessUrl("/")
                .usernameParameter("email")
                .failureUrl("/members/login/error")
                .and()
                .logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/members/logout"))

                .logoutSuccessHandler((request, response, authentication) -> {
                    // 인증 정보 확인
                    if (authentication != null && authentication.isAuthenticated()) {
                        String redirectUrl = determineRedirectUrl(authentication);

                        // 로그아웃 후에 리다이렉트
                        response.sendRedirect(redirectUrl);
                    } else {
                        // 인증되지 않았거나 인증 정보가 없는 경우
                        response.sendRedirect("/");
                    }
                })

                //.logoutSuccessUrl("/")
                .and()
                .oauth2Login()
                .userInfoEndpoint().userService(customOAuth2UserService);
        ;

        http.authorizeHttpRequests()

                // 홈,회원관련,상품,이미지 + 파비콘 + 카카오로그인 + 카테고리
                .mvcMatchers("/", "/members/**", "/member/**", "/item/**", "/images/**", "/image/**", "/favicon.ico", "/category/**")
                .permitAll() // 아무나 접근 가능

                .mvcMatchers("/admin/**") // 관리자 페이지
                .hasRole("ADMIN") // ADMIN만 접근 가능

                .anyRequest().authenticated(); // 나머지는 로그인 필요

        http.exceptionHandling() // 위 제시된 조건 외 발생시
                .authenticationEntryPoint(new CustomAuthenticationEntryPoint());
    }

    @Bean //빈객체(원두) -> SpringContainer 에 들어감
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(customUserDetailsService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**"); // css, js, img 관련 요청은 무시
    }

    private String determineRedirectUrl(Authentication authentication) throws IOException {


        if (authentication instanceof UsernamePasswordAuthenticationToken) { // 일반 로그인의 경우
            return "/";

        } else { // 소셜로그인의 경우
            OAuth2AuthenticationToken authToken = (OAuth2AuthenticationToken) authentication;
            String[] parsedToken = AuthTokenParser.getParseToken(authToken);
            // role 확인
            log.info("!OAuth2 role-loginType! : " + parsedToken[1]);

            if(parsedToken[1].equals("google")){
               //return "https://www.google.com/accounts/Logout?continue=https://appengine.google.com/_ah/logout?continue=https://www.kyj9447.kr/members/logout";

            } else if (parsedToken[1].equals("naver")) {
               //return "http://nid.naver.com/nidlogin.logout?";

            }else if (parsedToken[1].equals("kakao")) {
                // return "https://kauth.kakao.com/oauth/logout/?client_id=0ea9af982ecb374ececf50d24a8894d6&logout_redirect_uri=https://www.kyj9447.kr:9444/members/logout";
            }

        }
        return "/";
    }
}



