package com.shop.test;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class ConvertMultipartFile {

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

    public static MultipartFile convertToMultipartFile(byte[] bytes, String originalFileName, String contentType) {
        return new MockMultipartFile(originalFileName, originalFileName, contentType, bytes);
    }

}
