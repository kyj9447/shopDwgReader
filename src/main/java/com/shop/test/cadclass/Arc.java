package com.shop.test.cadclass;

public class Arc extends CadObject {

    // 생성자
    public Arc(double X, double Y, double R, double startA, double endA) {
        centerX = roundcut(X);
        centerY = roundcut(Y);
        radius = roundcut(R);
        startangle = roundcut(startA);
        endangle = roundcut(endA);

        startX = roundcut(X + R * Math.cos(Math.toRadians(startA)));
        startY = roundcut(Y + R * Math.sin(Math.toRadians(startA)));
        endX = roundcut(X + R * Math.cos(Math.toRadians(endA)));
        endY = roundcut(Y + R * Math.sin(Math.toRadians(endA)));

        length = roundcut(((R + R) * Math.PI) * Math.abs(startA - endA) / 360); // 둘레 * (일부각도 / 전체각도)
        area = roundcut((R * R * Math.PI) * Math.abs(startA - endA) / 360); // 면적 * (일부각도 / 전체각도)

        // 생성시 정보출력
        System.out.println("중심점 X:  " + centerX + " Y:  " + centerY);
        System.out.println("반지름 R:  " + radius);
        System.out.println("시작각 : " + roundcut(startA) + "º");
        System.out.println("끝도 : " + roundcut(endA) + "º");
        System.out.println("각도 : " + roundcut(Math.abs(startA - endA)) + "º");

        System.out.println("start   " + startX + ",     " + startY);
        System.out.println("end     " + endX + ",   " + endY);
        System.out.println("길이     " + length);
    }
}
