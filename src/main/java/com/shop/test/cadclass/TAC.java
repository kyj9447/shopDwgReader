package com.shop.test.cadclass;

import java.util.Comparator;

// Total Area Comparator
public class TAC implements Comparator<FinalObject> {
    @Override
    public int compare(FinalObject A, FinalObject B) {
        return Double.compare(B.totalarea, A.totalarea); // 내림차순
    }
}