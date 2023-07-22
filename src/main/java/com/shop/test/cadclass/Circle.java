package com.shop.test.cadclass;

public class Circle extends CadObject {

    // 생성자
    public Circle(double X, double Y, double R) {
        centerX = roundcut(X);
        centerY = roundcut(Y);
        radius = roundcut(R);

        if (radius*2 == 2 || radius*2 == 3 || radius*2 == 4 || radius*2 == 5 || radius*2 == 6 || radius*2 == 8 || radius*2 == 10
                || radius*2 == 12) { // 사용 가능한 드릴 직경들
            drillable = true;
        } else {
            drillable = false;
        }

        area = roundcut(R * R * Math.PI);
        length = roundcut((R + R) * Math.PI);

        // 생성시 정보출력
        System.out.println("중심점 X:  " + centerX + " Y:  " + centerY);
        System.out.println("반지름 R:  " + radius);
        System.out.println("드릴가능 :  " + drillable);

        System.out.println("길이 :  " + length);
        System.out.println("면적 :  " + area);
    }
}