package com.shop.controller;

import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.service.ItemService;
import com.shop.service.KakaoAPI;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MainController {
    private final ItemService itemService;
    @Autowired
    private KakaoAPI kakao;

    @GetMapping(value = "/")
    public String main(ItemSearchDto itemSearchDto, Optional<Integer> page, Model model) {
        log.info("SearchQuery : "+itemSearchDto.getSearchQuery());
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);
        if (itemSearchDto.getSearchQuery() == null) {
            itemSearchDto.setSearchQuery("");
        }
        Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);

        model.addAttribute("items", items);
        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 10);
        return "main";
    }

    // 카카오로그아웃 테스트
    @RequestMapping(value = "/kakaologout")
    public String kakaologout(HttpSession session) {

//        kakao.kakaoLogout((String) session.getAttribute("access_Token"));
//        session.removeAttribute("access_Token");
//        session.removeAttribute("userId");

        //kakao.kakaoLogout3();
        //return "redirect:/members/logout";
        return "redirect:https://kauth.kakao.com/oauth/logout?client_id=0ea9af982ecb374ececf50d24a8894d6&logout_redirect_uri=https://localhost/members/logout";
    }

    //카테고리 선택
    @GetMapping(value = "/category/{category}")
    public String category(@PathVariable("category") String category, ItemSearchDto itemSearchDto, Optional<Integer> page, Model model) {
        log.info("category "+category);
        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 10);

//        if (itemSearchDto.getSearchQuery() == null) {
//            itemSearchDto.setSearchQuery("");
//        }

        if(category.equals("all")){
            // Dto 에 카테고리 이름 세팅
            itemSearchDto.setSearchCategory(category);

            Page<MainItemDto> items = itemService.getMainItemPage(itemSearchDto, pageable);
            model.addAttribute("items", items);
        }

        else {
            // Dto 에 카테고리 이름 세팅
            itemSearchDto.setSearchCategory(category);

            // itemservice에 dto로 카테고리 아이템들 요청
            Page<MainItemDto> items = itemService.getCategoryPage(itemSearchDto, pageable);
            model.addAttribute("items", items);
        }


        model.addAttribute("itemSearchDto", itemSearchDto);
        model.addAttribute("maxPage", 10);
        return "category";
    }
}