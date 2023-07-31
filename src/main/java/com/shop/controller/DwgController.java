package com.shop.controller;

import com.google.gson.Gson;
import com.shop.dto.ItemFormDto;
import com.shop.service.ItemService;
import com.shop.test.DwgReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class DwgController {
    private final ItemService itemService;
    DwgReader dwgReader;

    @GetMapping(value = "/dwg")
    public String dwgIn(Model model) {
        String imagePath = "/image/reload.gif";
        model.addAttribute("loading", imagePath);
        return "/dwg/input";
    }

    @PostMapping(value = "/dwg")
    public String uploadDwg(MultipartFile dwgFile, Model model, @RequestParam("thick") int thick) throws IOException {

        // Gson 사용
        String json = new Gson().toJson(dwgReader.parseDwg(dwgFile));
        //System.out.println("제이슨 결과 = " + json);
        System.out.println("thick : " + thick);
        if (thick == 2) {
            model.addAttribute("plateprice", 0.2);
            model.addAttribute("cutprice", 20);
            model.addAttribute("drillprice", 500);
        } else if (thick == 5) {
            model.addAttribute("plateprice", 0.5);
            model.addAttribute("cutprice", 50);
            model.addAttribute("drillprice", 1300);
        } else { // thick == 10
            model.addAttribute("plateprice", 1);
            model.addAttribute("cutprice", 100);
            model.addAttribute("drillprice", 2800);
        }

        model.addAttribute("finalObjectList", json);
        model.addAttribute("dwgFile", dwgFile); // 주문버튼을 누를때를 대비해 파일도 다시 포함해 돌려줌
        model.addAttribute("itemFormDto", new ItemFormDto()); // 주문버튼을 누를때를 대비해 Dto 도 추가

        return "/dwg/output";
    }

    // 커스텀 상품 등록 + 해당상품 즉시 주문
    @PostMapping(value = "/dwg/output")
    public String buyCustomItem(@RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model, ItemFormDto itemFormDto) throws IOException {
        try {
            itemService.saveCustomItem(itemFormDto, itemImgFileList);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "redirect:/dwg";
        }
        return "redirect:/";
    }
}
