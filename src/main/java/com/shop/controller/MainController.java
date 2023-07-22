package com.shop.controller;

import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.service.ItemService;
import com.shop.service.KakaoAPI;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class MainController {
    private final ItemService itemService;
    @Autowired
    private KakaoAPI kakao;

    @GetMapping(value = "/")
    public String main(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model) {
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 5);
        if (itemSearchDto.getSearchQuery() == null) {
            itemSearchDto.setSearchQuery("");
        }
        Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);

        System.out.println(items.getNumber() + "!!!!!!!");
        System.out.println(items.getTotalPages() + "########");

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 5);
        return "main";
    }

    // 카카오로그인 테스트
    @RequestMapping(value = "/kakaologin")
    public String kakaologin(@RequestParam("code") String code, HttpSession session) {

        String access_Token = kakao.getAccessToken(code);
        HashMap<String, Object> userInfo = kakao.getUserInfo(access_Token);
        System.out.println("code : " + code);
        System.out.println("controller access_token : " + access_Token);
        System.out.println("login Controller : " + userInfo);

        // 클라이언트의 이메일이 존재할 때 세션에 해당 이메일과 토큰 등록
        if (userInfo.get("email") != null) {
            System.out.println("setAttribute : \"userId\" : " + userInfo.get("email"));
            session.setAttribute("userId", userInfo.get("email"));
            System.out.println("setAttribute : \"access_Token\" : " + access_Token);
            session.setAttribute("access_Token", access_Token);
            session.setAttribute("kakaoLoggedInUser", "true"); // 'kakaoUser'는 카카오 로그인한 사용자 정보를 나타내는 객체
        }

        // 토큰으로 받은 유저정보로 인증객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(userInfo, null, null);
        // 인증객체를 사용자목록에 추가
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return "redirect:/";
    }

    // 카카오로그아웃 테스트
    @RequestMapping(value = "/kakaologout")
    public String kakaologout(HttpSession session) {

//        kakao.kakaoLogout((String) session.getAttribute("access_Token"));
//        System.out.println("removeAttribute : \"access_Token\"");
//        session.removeAttribute("access_Token");
//        System.out.println("removeAttribute : \"userId\"");
//        session.removeAttribute("userId");

        kakao.kakaoLogout3();
        //return "redirect:/members/logout";
        return "redirect:https://kauth.kakao.com/oauth/logout?client_id=0ea9af982ecb374ececf50d24a8894d6&logout_redirect_uri=http://localhost/members/logout";
    }
}
