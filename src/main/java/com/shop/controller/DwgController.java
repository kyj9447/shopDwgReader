package com.shop.controller;

import com.google.gson.Gson;
import com.shop.test.DwgReader;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Controller
public class DwgController {
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
        System.out.println("thick : "+thick);
        if (thick == 2){
            model.addAttribute("plateprice", 0.2);
            model.addAttribute("cutprice", 20);
            model.addAttribute("drillprice", 500);
        }
        else if (thick == 5){
            model.addAttribute("plateprice", 0.5);
            model.addAttribute("cutprice", 50);
            model.addAttribute("drillprice", 1300);
        }
        else { // thick == 10
            model.addAttribute("plateprice", 1);
            model.addAttribute("cutprice", 100);
            model.addAttribute("drillprice", 2800);
        }

        model.addAttribute("finalObjectList", json);
        return "/dwg/output";
    }
}
