package controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
@Controller
public class YuZhi_decodeRecall {
    @RequestMapping(value = "/getJson", method = RequestMethod.POST)
    public @ResponseBody
    String  getJson(HttpServletRequest request) throws IOException {
//        System.out.println("JSON");
//        this.uuid = uuid;
//        this.ID = ID;
//        this.Decode = Decode;
//        File file = new File("C:\\Users\\59785\\Desktop\\json.txt");
//        BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file));
//        bufferedWriter.write(uuid + ID + Decode + "\r\n");
//        bufferedWriter.flush();
//        bufferedWriter.close();
        BufferedReader reader = request.getReader();
        char[] buf = new char[512];
        int len = 0;
        StringBuffer contentBuffer = new StringBuffer();
        while ((len = reader.read(buf)) != -1) {
            contentBuffer.append(buf, 0, len);
        }
        String content = contentBuffer.toString();
        if(content == null){
            content = "";
        }
        JSONObject jsonObject =JSONObject.parseObject(content);
        System.out.println(jsonObject.getString("Decode"));
        File file =new File("C:\\Users\\59785\\Desktop\\Decode.txt");
        BufferedWriter bw=new BufferedWriter(new FileWriter(file));
        bw.write(jsonObject.getString("Decode"));
        bw.flush();
        bw.close();
        return "ok";
    }

    @RequestMapping(value = "/get", method = RequestMethod.GET)
    public @ResponseBody
    String getRemoteHost(javax.servlet.http.HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip.equals("0:0:0:0:0:0:0:1") ? "127.0.0.1" : ip +"    hello world";
    }
}


