package controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.*;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Controller
public class YuZhi_decodeRecall {
    @RequestMapping(value = "/getJson", method = RequestMethod.POST)
    public @ResponseBody
    String  getJson(HttpServletRequest request) throws IOException {

        System.out.println("decode");
        BufferedReader reader = request.getReader();
        String string,content = "";
        while ((string = reader.readLine())!= null){
            content += string;
        }
//        Map<String, String> map = new HashMap<String, String>();
//        Enumeration headerNames = request.getHeaderNames();
//        while (headerNames.hasMoreElements()) {
//            String key = (String) headerNames.nextElement();
//            String value = request.getHeader(key);
//            map.put(key, value);
//        }
//        System.out.println(map);
//        System.out.println(map.get("decode_result"));
        JSONObject jsonObject =JSONObject.parseObject(content);
        System.out.println(jsonObject);
        if(Integer.parseInt(jsonObject.getString("return_code"))==0){
            System.out.println(jsonObject.getString("decode_result"));
            File file =new File("C:\\Users\\yzkj\\Desktop\\Decode.txt");
            BufferedWriter bw=new BufferedWriter(new FileWriter(file));
            bw.write(content);
            bw.flush();
            bw.close();
            return "ok";
        }
        else {
            System.out.println("error");
        }
        return "noting";
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


