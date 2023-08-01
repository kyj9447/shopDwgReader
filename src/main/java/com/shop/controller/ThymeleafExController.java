package com.shop.controller;

import com.shop.dto.ItemDto;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller // 현재 클래스 컨트롤러로 선언
@RequestMapping(value = "/thymeleaf") // 이 클래스의 시작경로 URL는 /thymeleaf
public class ThymeleafExController {
    @GetMapping( value = "/ex01") // GET /thymeleaf/ex01
    public String thymeleafExample01(Model model){ // Model객체 -> view 화면 세팅용 모델
        model.addAttribute("data","공부하세요!"); // 속성(data), 값(공부하세요) 추가
        return "thymeleafEx/thymeleafEx01" ; // 렌더링할 html 경로
    }

    @GetMapping(value = "/ex02")
    public String thymeleafExample02(Model model){
        ItemDto itemDto = new ItemDto();
        itemDto.setItemDetail("상품 상세 설명");
        itemDto.setItemName("테스트 상품1");
        itemDto.setPrice(10000);
        itemDto.setRegTime(LocalDateTime.now());

        model.addAttribute("itemDto",itemDto);
        return "thymeleafEx/thymeleafEx02";
    }

    @GetMapping(value = "/ex03")
    public String thymeleafExample03(Model model){
        List<ItemDto> itemDtoList = new ArrayList<>();
        for ( int i = 1 ; i<=10 ; i++ ) {
            ItemDto itemDto = new ItemDto();
            itemDto.setItemDetail("상품 상세 설명"+i);
            itemDto.setItemName("테스트 상품"+i);
            itemDto.setPrice(10000*i);
            itemDto.setRegTime(LocalDateTime.now());
            itemDtoList.add(itemDto);
        }
        model.addAttribute("itemDtoList",itemDtoList);
        return "thymeleafEx/thymeleafEx03";
    }

    @GetMapping(value = "/ex04")
    public String thymeleafExample04(Model model){
        List<ItemDto> itemDtoList = new ArrayList<>();
        for ( int i = 1 ; i<=10 ; i++ ) {
            ItemDto itemDto = new ItemDto();
            itemDto.setItemDetail("상품 상세 설명"+i);
            itemDto.setItemName("테스트 상품"+i);
            itemDto.setPrice(10000*i);
            itemDto.setRegTime(LocalDateTime.now());
            itemDtoList.add(itemDto);
        }
        model.addAttribute("itemDtoList",itemDtoList);
        return "thymeleafEx/thymeleafEx04";
    }

    @GetMapping(value = "/ex05")
    public String thymeleafExamples05(){
        return "thymeleafEx/thymeleafEx05";
    }

    @GetMapping(value = "/ex06")
    public String thymeleafExamples06(String param1, String param2, Model model){
        model.addAttribute("param1",param1);
        model.addAttribute("param2",param2);
        return "thymeleafEx/thymeleafEx06";
    }

    @GetMapping(value = "/ex07")
    public String thymeleafExamples07(){
        return "thymeleafEx/thymeleafEx07";
    }

}
