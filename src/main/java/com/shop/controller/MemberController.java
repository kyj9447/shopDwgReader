package com.shop.controller;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import com.shop.service.MailService;
import com.shop.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@Controller
@RequestMapping(value = "/members")
@RequiredArgsConstructor
@Slf4j
public class MemberController {
    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    // private final MailService mailService;
    // String confirm = "";
    // boolean confirmCheck = false;

    @GetMapping(value = "/new")
    public String memberFrom(Model model){
        model.addAttribute("memberFormDto",new MemberFormDto());
        return "member/memberForm";
    }

    @PostMapping(value = "/new")
    public String memberForm(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model){
        if(bindingResult.hasErrors()){
            return "member/memberForm";
        }
        try{
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            member.setLoginType("normal"); // 홈페이지 가입은 normal
            memberService.saveMember(member);
        }
        catch (IllegalStateException e){
            model.addAttribute("errorMessage",e.getMessage());
            log.info((String) model.getAttribute("errorMessage"));
            return "member/memberForm";
        }
        return "redirect:/";
    }

    @GetMapping(value = "/login")
    public String loginMember(){
        return "member/memberLoginForm";
    }

    @GetMapping(value = "/login/error")
    public String loginError(Model model){
        model.addAttribute("loginErrorMsg","아이디 혹은 비밀번호를 확인해주세요");
        return "member/memberLoginForm";
    }

    // // 메일인증
    // @PostMapping(value = "/{email}/emailConfirm")
    // public @ResponseBody ResponseEntity emailConfirm(@PathVariable("email") String email) throws Exception{
    //     log.info("인증요청 메일 : "+email);
    //     confirm = mailService.sendSimpleMessage(email);
    //     return new ResponseEntity<String>("인증 메일을 보냈습니다.", HttpStatus.OK);
    // }

    // // 메일인증 코드 체크
    // @PostMapping(value = "/{code}/codeCheck")
    // public @ResponseBody ResponseEntity codeConfirm(@PathVariable("code") String code) throws Exception {
    //     log.info("인증요청 코드 : "+code);
    //     if (code.equals(confirm)) {
    //         confirmCheck=true;
    //         return new ResponseEntity<String>("인증 성공하였습니다.",HttpStatus.OK);
    //     }
    //     return new ResponseEntity<String>("인증 코드를 확인해주세요.", HttpStatus.BAD_REQUEST);
    // }

}
