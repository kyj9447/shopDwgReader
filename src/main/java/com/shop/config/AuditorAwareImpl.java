package com.shop.config;

import com.shop.service.AuthTokenParser;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

public class AuditorAwareImpl implements AuditorAware<String> {
    @Override
    public Optional<String> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String userId = "";
        if (authentication != null) { // = principal 있으면
            if (authentication instanceof AnonymousAuthenticationToken) { // 익명사용자일경우 (ex. 회원가입시 member의 created_by는 익명이다)
                // 기존 코드
                userId = authentication.getName();
            } else {
                String[] auditor = AuthTokenParser.getParseToken(authentication);
                userId = auditor[0]+"/"+auditor[1]; // "이메일/type" 형식
            }
        }
        return Optional.of(userId);
    }
}
