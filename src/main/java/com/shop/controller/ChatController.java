package com.shop.controller;


import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class ChatController {

    @GetMapping(value = "/chat")
    public String requestChat(Principal principal){

        if(principal instanceof UsernamePasswordAuthenticationToken authToken){
            Object[] role = authToken.getAuthorities().toArray();
            String role2 = role[0].toString();
            if (role2.equals("ROLE_ADMIN")) {
                System.out.println("admin 채팅 return");
                return "chatAdmin";
            }
        }
        System.out.println("일반 채팅 return");

        return "chat";
    }
}
