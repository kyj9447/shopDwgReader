package com.shop.controller;

import com.google.gson.Gson;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemFormDto;
import com.shop.dto.OrderDto;
import com.shop.service.AuthTokenParser;
import com.shop.service.ItemService;
import com.shop.service.OrderService;
import com.shop.test.DwgReader;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static com.shop.test.ConvertMultipartFile.convertToMultipartFile;
import static com.shop.test.ConvertMultipartFile.stringToByteArray;


@Controller
@RequiredArgsConstructor
public class DwgController {
   private final ItemService itemService;
    private final OrderService orderService;

    @GetMapping(value = "/dwg")
    public String dwgIn(Model model) {
        String imagePath = "/image/reload.gif";
        model.addAttribute("loading", imagePath);
        return "dwg/input";
    }

    @PostMapping(value = "/dwg")
    public String uploadDwg(MultipartFile dwgFile, Model model, @RequestParam("thick") int thick) throws IOException {

        // Gson 사용
        String json = new Gson().toJson(DwgReader.parseDwg(dwgFile));
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

        String dwgString = Arrays.toString(dwgFile.getBytes()); // 파일을 바이트 배열로 바꾸고 긴 스트링으로 바꿈
        // System.out.println(dwgString);

        model.addAttribute("finalObjectList", json);

        model.addAttribute("returnDwgFile", dwgString); // 주문버튼을 누를때를 대비해 파일도 다시 포함해 돌려줌
        model.addAttribute("itemFormDto", new ItemFormDto()); // 주문버튼을 누를때를 대비해 Dto 도 추가
        model.addAttribute("thick", thick); // 상품명 생성을 위한 thick 값 추가

        return "dwg/output";
    }

    // 커스텀 상품 등록 + 해당상품 즉시 주문
    @PostMapping(value = "/dwg/output")
    public String buyCustomItem(
            @RequestParam("orderPrice") int orderPrice,
            @RequestParam("canvasImage") String canvasImage,
            @RequestParam("dwgFile") String dwgFile,
            @RequestParam("thick") int thick,
            Model model,
            Principal principal,
            ItemFormDto itemFormDto,
            RedirectAttributes redirectAttributes
            ) throws IOException {

        //        try {
        //            itemService.saveCustomItem(itemFormDto, itemImgFileList);
        //        } catch (Exception e) {
        //            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
        //            return "redirect:/dwg";
        //        }

        System.out.println("받은 바이트 " + dwgFile); // 받은 긴 스트링

        System.out.println("canvasImage : " + canvasImage);
        System.out.println("orderPrice : " + orderPrice);
        System.out.println("dwgFile : " + dwgFile);
        System.out.println("thick : " + thick);
        System.out.println("------------------------------------------------");

        // 캔버스 Base64 -> byte[]
        // byte[] canvasByteArray = Base64.getUrlDecoder().decode(canvasImage);
        //System.out.println(Arrays.toString(canvasByteArray));

        //byte[] canvasByteArray2 = Base64.getDecoder().decode(canvasImage);
        //System.out.println(Arrays.toString(canvasByteArray2));

        // 캔버스 Base64 -> byte[]
        String[] canvasImage2 = canvasImage.split(","); // 앞부분 제거 (data:image/png;base64,)
        byte[] canvasByteArray = DatatypeConverter.parseBase64Binary(canvasImage2[1]);
        System.out.println("canvasImage2 : " + Arrays.toString(canvasByteArray));

        // dwg String -> byte[]
        byte[] dwgByteArray = stringToByteArray(dwgFile);
        System.out.println("dwgByteArray : " + Arrays.toString(dwgByteArray));

        System.out.println("Make MultipartFile");
        MultipartFile canvasFile = convertToMultipartFile(canvasByteArray,"canvasImage.png","image/png");
        MultipartFile dwgFinalFile = convertToMultipartFile(dwgByteArray,"dwgFinalFile.dwg","application/acad");

        // Custom 아이템 추가
        System.out.println("Add to List<MultipartFile>");
        List<MultipartFile> itemFileList = new ArrayList<>();
        itemFileList.add(canvasFile);
        itemFileList.add(dwgFinalFile);

        System.out.println("setPrice itemFormDto");
        itemFormDto.setPrice(orderPrice);
        itemFormDto.setThick(thick);
        itemFormDto.setItemName(thick + "T 철판 주문 제작");
        itemFormDto.setItemCategory("custom");
        itemFormDto.setItemDetail("custom");
        itemFormDto.setItemSellStatus(ItemSellStatus.SELL);

        Long itemId = 1L;
        try {
            System.out.println("itemService saveCustomItem");
            itemId = itemService.saveCustomItem(itemFormDto, itemFileList);
            System.out.println("!itemId : " + itemId);
        } catch (Exception e) {
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
        }
        System.out.println("try catch end");

        /*--------------------------------------------------------------*/
        //커스텀 상품 등록 끝 -> 해당상품 즉시 주문
        System.out.println("주문할 상품 Id : " + itemId);

        OrderDto customOrderDto = new OrderDto();
        customOrderDto.setItemId(itemId);
        customOrderDto.setCount(1);

        String email = AuthTokenParser.getParseToken(principal)[0];
        String loginType = AuthTokenParser.getParseToken(principal)[1];

        orderService.customOrder(customOrderDto, email, loginType);

        String alertMessage = "주문이 완료됬습니다.";
        redirectAttributes.addFlashAttribute("alertMessage", alertMessage);

        return "redirect:/";
    }
}
