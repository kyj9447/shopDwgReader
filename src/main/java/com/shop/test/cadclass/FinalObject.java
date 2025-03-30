package com.shop.test.cadclass;

import java.util.ArrayList;
import java.util.List;

public class FinalObject{

    public List<CadObject> objectlist;
    public double totalarea;
    public double totallength;
    public boolean circle = false;
    public double canvassizeX;
    public double canvassizeY;
    // boolean ismainplate = false; List<FinalObject>를 totalarea 기준으로 오름차순 정렬하는것으로 대체

    public FinalObject(Circle A) { // 원 들어올 경우
        objectlist = new ArrayList<>();
        objectlist.add(A); // 그냥 추가
        totalarea = A.area;
        totallength = A.length;
        circle = true;
    }

    public FinalObject(List<CadObject> A) { // 완성된 닫힌 리스트 들어올경우
        objectlist = new ArrayList<>();
        objectlist.addAll(A);
        totallength = calcLength();
        totalarea = calcArea();
    }

    double calcLength() { // 총 길이 계산
        double result = 0;
        for (CadObject cadObject : objectlist) {
            result += cadObject.length;
        }
        return result;
    }

    double calcArea() { // 총 면적 계산
        // 결과값 선언
        double result = 0;

        // 필요한 배열 길이 계산
        int count = 1; // 좌표 갯수 카운트 / 시작점 1개
        for (CadObject cadObject : objectlist) {
            if (cadObject instanceof Arc) { // Arc인 경우
                count += 2; // 중심점, 끝점 추가
            } else { // Line인 경우
                count += 1; // 끝점 추가
            }
        }
        double[] shoeX = new double[count]; // X좌표 보관용
        double[] shoeY = new double[count]; // Y좌표 보관용 배열

        // 배열에 좌표 대입
        int j = 0;
        for (CadObject cadObject : objectlist) {
            if (cadObject instanceof Arc) { // Arc인 경우 중심점, 끝점 추가
                shoeX[j] = cadObject.centerX;
                shoeY[j] = cadObject.centerY;
                shoeX[j + 1] = cadObject.endX;
                shoeY[j + 1] = cadObject.endY;
                j += 2;
            } else { // Line인 경우 끝점 추가
                shoeX[j] = cadObject.endX;
                shoeY[j] = cadObject.endY;
                j += 1;
            }
        }

        // 배열 마직막에 첫점을 복사 (슈레이스 계산시 필요)
        shoeX[count-1] = shoeX[0];
        shoeY[count-1] = shoeY[0];

        // 슈레이스 계산 실행
        double sumA = 0;
        double sumB = 0;
        for (int i = 0 ; i < shoeX.length-1; i++ ){
            sumA += shoeX[i]*shoeY[i+1];
            sumB += shoeY[i]*shoeX[i+1];
        }
        result += Math.abs(sumA-sumB)/2; // 계산결과를 result에 추가

        // Object중 Arc 면적 합산
        for (CadObject cadObject : objectlist) {
            if (cadObject instanceof Arc) {
                result += cadObject.area;
            }
        }

        // // 배열 출력 TEST
        // for (double X : shoeX) {
        //     //log.debug(X+",");
        // }
        // //log.debug();
        // for (double Y : shoeY) {
        //     //log.debug(Y+",");
        // }
        // //log.debug("\ncheck2");

        return roundcut(result);
    }

    // 반올림 메서드 (불필요한 멤버 상속을 피하기 위한 메서드)
    double roundcut(double input) { // 1.123456789 -> 1.12 (소수 셋째자리 반올림)
        return (double) Math.round(input * 100) / 100;
    }
}