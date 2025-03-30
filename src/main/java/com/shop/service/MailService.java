package com.shop.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailService {
    private final JavaMailSender emailSender;
    private String ePw = "";

    private MimeMessage createMessage(String to) throws Exception{
        ePw = createKey();
        log.info("보내는 대상 : "+ to);
        log.info("인증 번호 : "+ ePw);
        MimeMessage message = emailSender.createMimeMessage();

        message.addRecipients(Message.RecipientType.TO, to); // 보내는 대상
        message.setSubject("회원가입 이메일 인증"); // 제목

        String msgg = "";
        msgg+="<div style='margin: 20px;'>";
        msgg+="<h1> 안녕하세요 공부하세요입니다.</h1><br><p>아래 코드를 복사해 입력해주세요</p><br><p>감사합니다.</p><br>";
        msgg+="<div align='center' style='border: 1px solid black; font-family: verdana;'>";
        msgg+="<h3 style='color: blue;'>회원가입 인증 코드입니다.</h3>";
        msgg+="<div style='font-size: 130%;'>CODE : <strong>"+ ePw +"</strong></div></div></div>";
        message.setText(msgg, "utf-8","html");
        message.setFrom(new InternetAddress("kyj9447@naver.com", "공브"));

        return message;
    }

    public static String createKey(){
        StringBuffer key = new StringBuffer();
        Random rnd = new Random();

        for (int i = 0 ; i<8 ; i++ ){
            int index = rnd.nextInt(3);

            switch (index){
                case 0:
                    key.append((char) ((int)(rnd.nextInt(26))+97));
                    break;
                case 1:
                    key.append((char) ((int)(rnd.nextInt(26))+65));
                case 2:
                    key.append((rnd.nextInt(10)));
                    break;
            }
        }
        return key.toString();
    }

    public String sendSimpleMessage (String to) throws Exception {
        MimeMessage message = createMessage(to);

        try {
            emailSender.send(message);
        } catch (MailException es){
            es.printStackTrace();;
            throw new IllegalArgumentException();
        }

        return ePw;
    }
}
