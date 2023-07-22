package com.shop.test.cadclass;

public class Line extends CadObject {

    // 생성자
    public Line(double X, double Y, double x, double y) {
        startX = roundcut(X);
        startY = roundcut(Y);
        endX = roundcut(x);
        endY = roundcut(y);

        length = roundcut(Math.sqrt((X - x) * (X - x) + (Y - y) * (Y - y)));

        // 생성시 정보출력
        System.out.println("start   " + startX + ",     " + startY);
        System.out.println("end     " + endX + ",       " + endY);
    }
}