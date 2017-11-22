//package controller;
//
//import org.springframework.stereotype.Component;
//
//import javax.websocket.*;
//import javax.websocket.server.ServerEndpoint;
//import java.io.BufferedWriter;
//import java.io.File;
//import java.io.FileWriter;
//import java.io.IOException;
//
//@Component
//@ServerEndpoint("/getMessage")
//    public class YuZhi_audioRecall {
//    private String YAEresult;
//    @OnOpen
//    public void onOpen() throws IOException {
//        System.out.println("open");
//    }
//    @OnMessage
//    public void onMessage(Session session,String message) throws IOException, InterruptedException {
//        System.out.println("message");
//        YAEresult=message;
//        System.out.println("Received: " + YAEresult);
//        File file = new File("C:\\Users\\59785\\Desktop\\1.txt");
//        BufferedWriter out = new BufferedWriter(new FileWriter(file));
//        out.write(YAEresult);
//        out.flush(); // 把缓存区内容压入文件
//        out.close();
////        session.getBasicRemote().sendText("This is the first server message");
////        int sentMessages = 0;
////        while (sentMessages < 3) {
////            Thread.sleep(5000);
////            session.getBasicRemote().sendText("This is an intermediate server message. Count: " + sentMessages);
////            sentMessages++;
////        }
////        session.getBasicRemote().sendText("This is the last server message");
//    }
//
//    @OnError
//    public void onError(Throwable throwable,Session session) {
//        System.out.println("error");
//        throwable.printStackTrace();
//    }
//    @OnClose
//    public void onClose() {
//        System.out.println("close");
//
//    }
//
//    public String getYAEresult() {
//        return YAEresult;
//    }
//}