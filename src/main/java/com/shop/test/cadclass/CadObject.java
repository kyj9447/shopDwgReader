package com.shop.test.cadclass;

public class CadObject {

    // 기초정보
    public double startX;
    public double startY;
    public double endX;
    public double endY;
    public double centerX;
    public double centerY;
    public double radius;
    boolean drillable;
    double startangle;
    double endangle;

    // 계산정보
    double length;
    double area;

    // 공용 반올림 메서드
    double roundcut(double input) { // 1.123456789 -> 1.12 (소수 셋째자리 반올림)
        double output = (double) Math.round(input * 100) / 100;
        return output;
    }
}