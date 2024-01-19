package com.shop.test.cadclass;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Circle extends CadObject {

    // 생성자
    public Circle(double X, double Y, double R) {
        centerX = roundcut(X);
        centerY = roundcut(Y);
        radius = roundcut(R);

        // 사용 가능한 드릴 직경들
        drillable = radius * 2 == 2 || radius * 2 == 3 || radius * 2 == 4 || radius * 2 == 5 || radius * 2 == 6 || radius * 2 == 8 || radius * 2 == 10
                || radius * 2 == 12;

        area = roundcut(R * R * Math.PI);
        length = roundcut((R + R) * Math.PI);

        // 생성시 정보출력
        log.debug("중심점 X:  " + centerX + " Y:  " + centerY);
        log.debug("반지름 R:  " + radius);
        log.debug("드릴가능 :  " + drillable);

        log.debug("길이 :  " + length);
        log.debug("면적 :  " + area);
    }
}