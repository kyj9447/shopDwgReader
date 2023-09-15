package com.shop.service;

import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class ChatSocketHandler extends TextWebSocketHandler {

    //private static List<WebSocketSession> userList = new ArrayList<>();
    private static Map<String, WebSocketSession> userList = new HashMap<>(); // <메일+type : 세션>
    private static List<WebSocketSession> adminList = new ArrayList<>();

    // 메세지 처리
    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {

        /*---------------------------------------[공통 처리사항]-------------------------------------*/
        //String sessionId = session.getId();
        //String[] identificationRaw = AuthTokenParser.getParseToken(session.getPrincipal());
        //String identification = identificationRaw[0]+","+identificationRaw[1];
        String payload = message.getPayload();
        boolean isAdmin = AuthTokenParser.isAdmin(session.getPrincipal());

        //System.out.println("sessionId : " + sessionId);
        //System.out.println("identification : " + identificationRaw[0] + " / type : " + identificationRaw[1]);
        System.out.println("payload : " + payload);
        System.out.println("admin? : " + isAdmin);

        /*---------------------------------------[공통 처리사항]-------------------------------------*/

        if (isAdmin) { // 관리자 -> 특정 사용자

            try {
                String[] Message = message.getPayload().split(",", 3); // {이메일,type,내용} 내용에 쉼표가 있어도 나누지 않고 하나로 처리
                System.out.println("name : " + Message[0]);
                System.out.println("type : " + Message[1]);
                System.out.println("message : " + Message[2]);

                String targetUser = Message[0] + "," + Message[1]; // "email,type" 형식으로 한개의 String 생성
                WebSocketSession targetSession = userList.get(targetUser); // 해당 String을 key로 하여 value(=session) 찾음
                if (targetSession == null) { // 타겟 세션이 없으면
                    WebSocketMessage<String> errorMessage = new TextMessage("상담 대상이 없습니다.");
                    session.sendMessage(errorMessage); // 관리자에게 에러메세지를 되돌려 보낸다
                } else { // 타겟 세션이 있으면 메세지 내용물 보냄
                    //targetSession.sendMessage(new TextMessage(Message[0]+",admin,"+Message[2])); //{이메일,admin,내용}
                    targetSession.sendMessage(new TextMessage(Message[0] + ",admin," + Message[2])); //{내용}
                }
            }
            catch (Exception e){ // 메세지 전송 실패시
                WebSocketMessage<String> errorMessage = new TextMessage("메세지 전송에 실패했습니다.");
                session.sendMessage(errorMessage); // 관리자에게 에러메세지를 되돌려 보낸다
            }

        } else { // 사용자 -> 관리자
            if (adminList.size() != 0) { // 관리자가 한명이라도 있으면
                String[] userInfo = AuthTokenParser.getParseToken(session.getPrincipal());
                for (WebSocketSession sessions : adminList) { // 모든 admin에게 브로드캐스트
                    // {이메일, type, 메세지 내용}
                    WebSocketMessage<String> userMessage = new TextMessage(userInfo[0] + "," + userInfo[1] + "," + message.getPayload());
                    sessions.sendMessage(userMessage);
                }
            } else { // 관리자가 없으면
                WebSocketMessage<String> errorMessage = new TextMessage("현재 상담시간이 아닙니다.");
                session.sendMessage(errorMessage); // 메세지를 보낸 세션에 에러메세지를 되돌려 보낸다
            }
        }
    }

    // 연결후
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {

        if (AuthTokenParser.isAdmin(session.getPrincipal())) { // 관리자 연결시
            adminList.add(session);
        } else { // 사용자 연결시
            if (adminList.size() != 0) { // 관리자가 한명이라도 있으면
                String[] userInfo = AuthTokenParser.getParseToken(session.getPrincipal());
                String userInfoString = userInfo[0]+","+userInfo[1]; // "이메일,type" 형식
                System.out.println("새 사용자 등록 : "+userInfoString);
                userList.put(userInfoString, session); // 사용자 목록에 추가하고
                for (WebSocketSession sessions : adminList) { // 모든 admin에게 알림
                    WebSocketMessage<String> newUserMessage =
                            // {이메일, type, 메세지 내용}
                            new TextMessage(userInfo[0] + "," + userInfo[1] + "," + userInfo[0] + " 님이 상담을 요청했습니다.");
                    sessions.sendMessage(newUserMessage);
                }
            } else { // 관리자가 없으면
                System.out.println("!현재 접속중인 관리자가 없어 채팅을 닫습니다.!");
                WebSocketMessage<String> errorMessage = new TextMessage("현재 상담시간이 아닙니다.");
                session.sendMessage(errorMessage); // 접속시도한 세션에 에러메세지를 되돌려 보낸다
                session.close(); // 세션 닫음
            }
        }

        //System.out.println("현재 접속 session");

        //System.out.println("admin session");
        //for (WebSocketSession sessions : adminList) {
        //    System.out.println(sessions);
        //}

        //System.out.println("user session");
        //userList.forEach((key, value) -> System.out.println("키: " + key + "/ 값: " + value));

    }

    // 연결 종료 후
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {

        if (AuthTokenParser.isAdmin(session.getPrincipal())) { // 관리자가 연결 종료시
            adminList.remove(session); // 관리자 목록에서 삭제
        } else { // 사용자가 연결 종료시
            String[] userInfo = AuthTokenParser.getParseToken(session.getPrincipal());
            String userInfoString = userInfo[0]+","+userInfo[1]; // "이메일,type" 형식
            System.out.println("사용자 연결 종료 : "+userInfoString);
            for (WebSocketSession sessions : adminList) { // 모든 admin에게 알리고
                WebSocketMessage<String> endUserMessage =
                        // {이메일, type, 메세지 내용}
                        new TextMessage(userInfo[0] + "," + userInfo[1] + "," + userInfo[0] + " 님이 상담을 종료했습니다.");
                sessions.sendMessage(endUserMessage);
            }

            userList.remove(session); // 목록에서 삭제
        }
        System.out.println(session + " 클라이언트 접속 해제");
    }

    // 이메일+type 으로 session 찾는 메서드
    public WebSocketSession findSession(String email, String type){
        return userList.get(new String[]{email, type});
    }
}