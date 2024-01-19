package com.shop.service;

import com.shop.entity.Member;
import com.shop.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@RequiredArgsConstructor // final 혹은 @NotNull 명령어가 붇으면 객체를 컨테이너에서 자동으로 연결해줌
@Slf4j
public class MemberService implements UserDetailsService {
    private final MemberRepository memberRepository;

    public Member saveMember(Member member) {
        validateDuplicateMember(member);
        return memberRepository.save(member);
    }

    private void validateDuplicateMember(Member member) {
        log.info("member.getEmail() : "+member.getEmail());
        log.info("member.getLoginType() : "+member.getLoginType());
        Member findMember = memberRepository.findByEmailAndLoginType(member.getEmail(), member.getLoginType());
        if (findMember != null) {
            throw new IllegalStateException("이미 가입된 회원입니다.");
        }
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        log.info("!loadUserByUsername!");
        Member member = memberRepository.findByEmail(email);
        if (member == null){
            throw new UsernameNotFoundException(email);
        }

        return User.builder().username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString())
                .build();
    }



}
