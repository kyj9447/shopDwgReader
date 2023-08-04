package com.shop.config;

import com.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
// WebSecurityConfigurerAdapter 를 상속받는 클래스에
// @EnableWebSecurity를 선언하면
// SpringSecurityfilterChain이 자동 포함 메소드를 오버라이딩 가능
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    MemberService memberService;
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
                .logoutSuccessUrl("/")
                .and()
                .oauth2Login()
                .userInfoEndpoint().userService(customOAuth2UserService);;

        http.authorizeHttpRequests()

                // 홈,회원관련,상품,이미지 + 파비콘 + 카카오로그인 + 카테고리
                .mvcMatchers("/", "/members/**","/member/**", "/item/**", "/images/**","/image/**","/favicon.ico","/kakaologin","/category/**")
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
        auth.userDetailsService(memberService)
                .passwordEncoder(passwordEncoder());
    }

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/css/**", "/js/**", "/img/**"); // css, js, img 관련 요청은 무시
    }
}
