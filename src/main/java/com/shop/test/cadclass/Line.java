package com.shop.test.cadclass;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Line extends CadObject {

    // 생성자
    public Line(double X, double Y, double x, double y) {
        startX = roundcut(X);
        startY = roundcut(Y);
        endX = roundcut(x);
        endY = roundcut(y);

        length = roundcut(Math.sqrt((X - x) * (X - x) + (Y - y) * (Y - y)));

        // 생성시 정보출력
        log.debug("start   " + startX + ",     " + startY);
        log.debug("end     " + endX + ",       " + endY);
    }
}