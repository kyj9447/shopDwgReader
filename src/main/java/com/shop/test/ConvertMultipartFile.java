package com.shop.test;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Base64;
import java.util.Scanner;

public class ConvertMultipartFile {
    MockMultipartFile mockMultipartFile;

    // String -> byte[]
    public static byte[] stringToByteArray(String input) {
        // 문자열에서 숫자 부분만 추출하여 문자열 배열로 변환합니다.
        String[] numbersStr = input
                .replace("[", "")
                .replace("]", "")
                .replace(",", "")
                .split(" ");
        byte[] byteArray = new byte[numbersStr.length];

        // 각 숫자 문자열을 바이트로 변환하여 byteArray에 저장합니다.
        for (int i = 0; i < numbersStr.length; i++) {
            byteArray[i] = Byte.parseByte(numbersStr[i]);
        }

        return byteArray;
    }

    // byte[] -> MultipartFile
    public static MultipartFile byteToMultipart(byte[] input) {

        //byte[] -> inputStream
        // InputStream inputStream = new ByteArrayInputStream(decodedBytes);

        String fileName = "TEMP_NAME";
        // String contentType = "";
        MultipartFile multipartFile = new MockMultipartFile(fileName, input);

        return multipartFile;
    }

    public static MultipartFile convertToMultipartFile(byte[] bytes, String originalFileName, String contentType) throws IOException {
        return new MockMultipartFile(originalFileName, originalFileName, contentType, bytes);
    }

}
