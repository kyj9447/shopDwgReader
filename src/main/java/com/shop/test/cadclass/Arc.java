package com.shop.test.cadclass;

import lombok.extern.slf4j.Slf4j;

@Slf4j
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
        log.debug("중심점 X:  " + centerX + " Y:  " + centerY);
        log.debug("반지름 R:  " + radius);
        log.debug("시작각 : " + roundcut(startA) + "º");
        log.debug("끝도 : " + roundcut(endA) + "º");
        log.debug("각도 : " + roundcut(Math.abs(startA - endA)) + "º");

        log.debug("start   " + startX + ",     " + startY);
        log.debug("end     " + endX + ",   " + endY);
        log.debug("길이     " + length);
    }
}
