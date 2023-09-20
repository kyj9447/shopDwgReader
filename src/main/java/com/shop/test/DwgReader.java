package com.shop.test;

import com.aspose.cad.Image;
import com.aspose.cad.fileformats.cad.CadImage;
import com.aspose.cad.fileformats.cad.cadobjects.CadArc;
import com.aspose.cad.fileformats.cad.cadobjects.CadBaseEntity;
import com.aspose.cad.fileformats.cad.cadobjects.CadCircle;
import com.aspose.cad.fileformats.cad.cadobjects.CadLine;
import com.aspose.cad.fileformats.cad.cadobjects.polylines.CadPolyline;
import com.aspose.cad.fileformats.cad.cadobjects.vertices.Cad2DVertex;
import com.shop.test.cadclass.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
public class DwgReader {

//    static String openclose(Short a) {
//        if (a == 0) {
//            return "Opened";
//        } else {
//            return "Closed";
//        }
//    }

    // 리스트 내 좌표를 X,Y만큼 이동시키는 함수
    static void moveAll(List<FinalObject> A, double X, double Y) {
        for (FinalObject finalObject : A) {
            for (CadObject cadObject : finalObject.objectlist) {
                if (cadObject instanceof Circle) {
                    cadObject.centerX += X;
                    cadObject.centerY += Y;
                } else if (cadObject instanceof Arc) {
                    cadObject.centerX += X;
                    cadObject.centerY += Y;
                    cadObject.startX += X;
                    cadObject.startY += Y;
                    cadObject.endX += X;
                    cadObject.endY += Y;
                } else {
                    cadObject.startX += X;
                    cadObject.startY += Y;
                    cadObject.endX += X;
                    cadObject.endY += Y;
                }
            }
        }
    }

    /*--------------------------------------------------[이하메인]----------------------------------------------------- */

    public static List<FinalObject> parseDwg(MultipartFile DWG) throws IOException {

        // STEP0 파일 받기/ 열기
        // STEP1 파싱
        //
        // 1)일반 line, cirle, arc
        // line (시작점 a(x,y), 끝점 b(x,y))
        // circle (중심점 c(x,y), 반지름 r(x))
        // arc (중심점 c(x,y), 반지름 r(x))
        //
        // 2)복합 폴리라인
        // (시작점 a(x,y), 다음점 b(x,y), 다음점 c(x,y)... 마지막점 z(x,y),[시작점 a(x,y)?] )
        // 열린 폴리라인 => line으로 분해

        // STEP2
        // 객체 생성 후
        // List<CadObject> A에 담기 (partList)
        // Circle, 닫힌 폴리라인 제외 -> 바로 FinalObject 작성

        // STEP3
        // 1.List<CadObject> B 생성 후 A에서 한 객체를 빼옴 (=B가 비어있으면)
        // 2.B의 시작점 혹은 끝점과 일치하는 객체를 A에서 계속 빼옴 (while문 사용)
        // 3.while문이 1회 돌았는데 더이상 조건에 맞는(=가져올) 객체가 없을경우 while문 종료
        // 4-1.시작점-끝점 일치시 B를 매개변수로 하여 FinalObject객체 1개 생성 !!깊은복사 필수!!
        // -> Arc의 경우 FinalObject의 면적계산 메서드 실행시 시작점,중심점,끝점을 넘기고 자체 부채꼴면적 더함
        // 4-2.시작점-끝점 불일치시 작업 없이 다음 진행
        // 5.B내용 그대로 삭제 (허수들 삭제됨) [ B.clear() ] !!깊은복사 필수!!
        // A에 Circle객체 외 다른객체 존재시 while문 다시 시작

        // STEP4
        // finalList내 FinalObject객체의 totalarea값 기준 내림차순 정렬
        // (Comparator 사용 / totalarea가 가장 큰 객체가 배열의 첫번째로 옴)
        // JSON으로 변환

        // DWG 파일 경로
        // String dwgPath =
        // "D:\\kyj9447\\Spring\\shop\\src\\main\\java\\com\\shop\\test\\test.dwg";

        /*-------------------------[파일 받기]-------------------------*/
//        // MultipartFile 로 받은 파일 임시 저장
//        byte[] fileData = DWG.getBytes(); // 받은 파일의 데이터
//
//        // 임시파일 경로 지정
//        String directoryPath = "/temp"; // !D:/temp!
//
//        // 경로 생성
//        File directory = new File(directoryPath);
//        if (!directory.exists()) {
//            if (directory.mkdirs()) {
//                //System.out.println("디렉터리 생성함");
//            } else {
//                //System.out.println("디렉터리 생성 실패함");
//            }
//        } else {
//            //System.out.println("디렉터리 존재함");
//        }
//
//        // 임시파일 지정
//        String filePath = "/temp/temp.dwg";
//
//        // 파일쓰기 스트림 생성
//        FileOutputStream fos = new FileOutputStream(filePath);
//        fos.write(fileData); // 파일 쓰기 실행
//        fos.close(); //


        /*-------------------------[파일 받기 끝]-------------------------*/

        // DWG 파일을 읽어옴 (로컬파일 대신 스트림 입력함)
        CadImage cadImage = (CadImage) Image.load(DWG.getInputStream());

        // CAD 이미지의 엔티티 수를 확인
        //int entityCount = cadImage.getEntities().length;
        //System.out.println("Entity count: " + entityCount);

        // 엔티티들을 배열에 입력
        CadBaseEntity[] objectlist = cadImage.getEntities();

        // 사용할 리스트들 작성
        List<CadObject> partList = new ArrayList<>(); // 분해된 객체들
        List<CadObject> partList2 = new ArrayList<>(); // 조립작업용
        List<FinalObject> finalList = new ArrayList<>(); // 조립 완성된 최종객체들

        //System.out.println("CHECK2");

        // 클래스별 정보출력 및 처리
        for (CadBaseEntity cadBaseEntity : objectlist) {
            // CadLine
            if (cadBaseEntity instanceof CadLine) {
                //System.out.println("\n[캐드라인] " + objectlist[i]);

                partList.add(new Line(
                        (((CadLine) cadBaseEntity).getFirstPoint().getX()),
                        (((CadLine) cadBaseEntity).getFirstPoint().getY()),
                        (((CadLine) cadBaseEntity).getSecondPoint().getX()),
                        (((CadLine) cadBaseEntity).getSecondPoint().getY())));
            }

            // CadArc
            // !순서 중요!
            // CadArc -> CadCircle 순
            else if (cadBaseEntity instanceof CadArc) {
                //System.out.println("\n[캐드아크] " + objectlist[i]);

                partList.add(new Arc(
                        ((CadArc) cadBaseEntity).getCenterPoint().getX(),
                        ((CadArc) cadBaseEntity).getCenterPoint().getY(),
                        ((CadArc) cadBaseEntity).getRadius(),
                        ((CadArc) cadBaseEntity).getStartAngle(),
                        ((CadArc) cadBaseEntity).getEndAngle()));
            }

            // CadCircle
            // !순서 중요!
            // CadArc는 부모가 CadCircle이기 때문에 CadArc instanceof CadCircle => true 가 된다
            // objectlist[i].getClass() == CadCircle 사용불가
            // (오른쪽이 객체가 없음 / CadCircle.getClass() 안됨)
            // objectlist[i].getClass().toString() == "CadCircle" 안됨
            else if (cadBaseEntity instanceof CadCircle) {
                //System.out.println("\n[캐드서클] " + objectlist[i]);

                partList.add(new Circle(
                        ((CadCircle) cadBaseEntity).getCenterPoint().getX(),
                        ((CadCircle) cadBaseEntity).getCenterPoint().getY(),
                        ((CadCircle) cadBaseEntity).getRadius()));
            }

            // CadPolyline
            else if (cadBaseEntity instanceof CadPolyline) {
                //System.out.println("\n[캐드폴리라인] " + objectlist[i] + "\n");
                CadPolyline x = (CadPolyline) cadBaseEntity;

                // PolyLine line으로 분해
                for (int j = 1; j < cadBaseEntity.getChildObjects().size(); j++) {
                    if (cadBaseEntity.getChildObjects().get(j) instanceof Cad2DVertex) {
                        double startX = ((Cad2DVertex) cadBaseEntity.getChildObjects().get(j - 1)).getLocationPoint().getX();
                        double startY = ((Cad2DVertex) cadBaseEntity.getChildObjects().get(j - 1)).getLocationPoint().getY();
                        double endX = ((Cad2DVertex) cadBaseEntity.getChildObjects().get(j)).getLocationPoint().getX();
                        double endY = ((Cad2DVertex) cadBaseEntity.getChildObjects().get(j)).getLocationPoint().getY();

                        partList.add(new Line(startX, startY, endX, endY));
                    }
                }

                // 닫혀있는지 확인 // CadPolyline.getFlag() = 1 (닫힘) / 0 (열림)
                //System.out.println(openclose(x.getFlag()));
                // 닫힌 PolyLine 마지막 line 추가
                if (x.getFlag() == 1) {

                    // 첫 시작점 => 끝점 / 마지막 끝점 => 시작점으로하여 1개 추가
                    double startX = ((Cad2DVertex) cadBaseEntity.getChildObjects().get(cadBaseEntity.getChildObjects().size() - 2)).getLocationPoint().getX();
                    double startY = ((Cad2DVertex) cadBaseEntity.getChildObjects().get(cadBaseEntity.getChildObjects().size() - 2)).getLocationPoint().getY();
                    double endX = ((Cad2DVertex) cadBaseEntity.getChildObjects().get(0)).getLocationPoint().getX();
                    double endY = ((Cad2DVertex) cadBaseEntity.getChildObjects().get(0)).getLocationPoint().getY();

                    partList.add(new Line(startX, startY, endX, endY));
                }
            }

            // 기타
            else {
                System.out.println("\nother " + cadBaseEntity.getClass().toString());
            }
        }

        // Circle을 모두 찾아서 FinalObject 만든 후 partList에서 제거
        for (int i = 0; i < partList.size(); i++) {
            if (partList.get(i) instanceof Circle) {
                finalList.add(new FinalObject((Circle) partList.get(i)));
                partList.remove(i);
                i--;
            }
        }

        while (partList.size() != 0) { // A가 빌때까지

            if (partList2.size() == 0) { // 만약 B가 비어있으면
                partList2.add(partList.get(0)); // A의 첫번째를 B에 넣고
                partList.remove(0); // A의 첫번째 삭제
            }

            for (int i = 0; i < partList.size(); i++) { // A를 스캔
                double lastEndX = partList2.get(partList2.size() - 1).endX;
                double lastEndY = partList2.get(partList2.size() - 1).endY;
                double nextStartX = partList.get(i).startX;
                double nextStartY = partList.get(i).startY;
                double nextEndX = partList.get(i).endX;
                double nextEndY = partList.get(i).endY;

                if (lastEndX == nextStartX && lastEndY == nextStartY) {
                    // B의 마지막 객체의 끝점 == A의 객체의 시작점 (시작-끝-시작-끝 / 순방향)
                    partList2.add(partList.get(i)); // B에 담고
                    partList.remove(i); // A에서 삭제
                    i = -1;
                    continue; // 현재 스캔 종료 후 다시
                }
                if (lastEndX == nextEndX && lastEndY == nextEndY) {
                    // B의 마지막 객체의 끝점 == A의 객체의 끝점 (시작-끝-끝-시작 / 역방향)
                    // start, end 스왑 후 저장
                    double tempstartX = partList.get(i).endX;
                    double tempstartY = partList.get(i).endY;
                    partList.get(i).endX = partList.get(i).startX;
                    partList.get(i).endY = partList.get(i).startY;
                    partList.get(i).startX = tempstartX;
                    partList.get(i).startY = tempstartY;

                    partList2.add(partList.get(i)); // B에 담고
                    partList.remove(i); // A에서 삭제
                    i = -1;
                    // 현재 스캔 종료 후 다시 다음스캔 시작
                }
            }

            // break가 걸리지 않고 끝남 => finaList를 만들수 있는 partList2인지 확인
            if (partList2.get(0).startX == partList2.get(partList2.size() - 1).endX
                    && partList2.get(0).startY == partList2.get(partList2.size() - 1).endY) {
                // 첫 객체 시작점 == 끝 객체 끝점 -> 완성
                finalList.add(new FinalObject(partList2));
                partList2.clear();
            } else { // 첫 객체 시작점 != 끝 객체 끝점 -> 삭제
                partList2.clear();
            }
        }

        finalList.sort(new TAC()); // totalarea 큰 순으로 정렬

        // 첫번째 Object의 중심좌표 계산 후 0,0기준으로 이동
        double tempCenterX = 0;
        double tempCenterY = 0;
        int pointcount = 0;

        FinalObject mainObject = finalList.get(0); // 면적이 제일 넓은 객체 1개

        for (CadObject A : mainObject.objectlist) {
            if (A instanceof Circle) {
                tempCenterX = A.centerX;
                tempCenterY = A.centerY;
            } else {
                tempCenterX += A.startX;
                tempCenterY += A.startY;
            }
            pointcount++;
        }

        // 중심점 계산
        double mainCenterX = tempCenterX / pointcount;
        double mainCenterY = tempCenterY / pointcount;

        moveAll(finalList, -mainCenterX, -mainCenterY); // 0,0으로 이동

        // canvas 사이즈 계산
        double mainMaxX = 0;
        double mainMaxY = 0;
        double mainMinX = 0;
        double mainMinY = 0;
        double Xsize;
        double Ysize;

        // 모든 점들의 X 최대, 최소값, Y 최대, 최소값 구하기
        for (CadObject A : mainObject.objectlist) {
            if (A instanceof Arc || A instanceof Circle) {
                if (A.centerX+A.radius > 0 && A.centerX+A.radius > mainMaxX) {
                    mainMaxX = A.centerX+A.radius;
                }
                if (A.centerX-A.radius < 0 && A.centerX-A.radius < mainMinX) {
                    mainMinX = A.centerX-A.radius;
                }
                if (A.centerY+A.radius > 0 && A.centerY+A.radius > mainMaxY) {
                    mainMaxY = A.centerY+A.radius;
                }
                if (A.centerY-A.radius < 0 && A.centerY-A.radius < mainMinY) {
                    mainMinY = A.centerY-A.radius;
                }
            } else {
                if (A.startX > 0 && A.startX > mainMaxX) {
                    mainMaxX = A.startX;
                } else if (A.startX < 0 && A.startX < mainMinX) {
                    mainMinX = A.startX;
                }

                if (A.startY > 0 && A.startY > mainMaxY) {
                    mainMaxY = A.startY;
                } else if (A.startY < 0 && A.startY < mainMinY) {
                    mainMinY = A.startY;
                }
            }
        }

        // x축, y축 최대 길이 각각 저장
        Xsize = mainMaxX - mainMinX;
        Ysize = mainMaxY - mainMinY;

        mainObject.canvassizeX = Xsize+20; // 직경 10mm 엔드밀 2회분
        mainObject.canvassizeY = Ysize+20;

        moveAll(finalList, -mainMinX+10, -mainMinY+10); // X양수,Y양수영역으로 이동
        // moveAll(finalList, 1000, 1000); // 2000x2000 캔버스 중심으로 이동

        //System.out.println("\n최종출력");
        //System.out.println("partList " + partList);
        //System.out.println("partList2 " + partList2);
        //System.out.println("finalList " + finalList);

        // 메모리 해제
        cadImage.dispose();

        return finalList;
    }
}