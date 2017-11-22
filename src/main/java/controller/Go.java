package controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.ws.Response;
import java.io.*;
import java.util.Arrays;

@Controller
public class Go {
    @Autowired
    private ALi ALi;
    @Autowired
    private XunFei xunFei;
    @Autowired
    private BaiDu baiDu;

    private String filename;

    @RequestMapping(value = "/file", method = RequestMethod.POST)
    public void fileUpload2(@RequestParam(value = "file") MultipartFile file) throws Exception {
        try {
            filename = file.getOriginalFilename();
            File fileBaidu=new File(file.getOriginalFilename());
            System.out.println(file.getOriginalFilename());
            InputStream fisBaidu=file.getInputStream();
            byte[] bytes = new byte[4800];
            int len;
            OutputStream os = new FileOutputStream(fileBaidu);
            while ((len = fisBaidu.read(bytes)) != -1 ){
                os.write(bytes,0,len);
            }
            os.close();
            fisBaidu.close();
            System.out.println(fileBaidu.getAbsolutePath());
            baiDu.getToken(fileBaidu);
            System.out.println("````````````````````````````````````````````````````````");

            InputStream fisXunFei = file.getInputStream();
            xunFei.RecognizePcmfileByte(fisXunFei);
            fisXunFei.close();
            System.out.println("```````````````````````````````````````````````````````");

            InputStream fisALi = file.getInputStream();
            ALi.startAsr(fisALi);
            fisALi.close();
            System.out.println("```````````````````````````````````````````````````````");
        }
        catch (Exception e){
            e.printStackTrace();
        }



    }

    @RequestMapping(value = "/file", method = RequestMethod.GET)
    public @ResponseBody
    JSONObject view() throws IOException {
        JSONObject json = new JSONObject();
        JSONObject json_engine_one = new JSONObject();
        JSONObject json_engine_two = new JSONObject();
//        JSONObject json_engine_three = new JSONObject();
        JSONObject json_engine_four = new JSONObject();
        json_engine_one.put("engine", "ali");
        json_engine_one.put("sentence", ALi.getResult());
        System.out.println(ALi.getResult()+"                 ali");
        json_engine_two.put("engine", "xunfei");
        json_engine_two.put("sentence", xunFei.getResult());
        System.out.println(xunFei.getResult()+"               xunfei");
//        json_engine_three.put("engine","yuzhi");
//        json_engine_three.put("sentence",yaeresult);
        json_engine_four.put("engine","baidu");
        json_engine_four.put("sentence",baiDu.getResult1());
        System.out.println(baiDu.getResult1()+"               baidu");
        JSONArray array = new JSONArray();
        array.add(json_engine_one);
        array.add(json_engine_two);
//        array.add(json_engine_three);
        array.add(json_engine_four);
        json.put("result", array);
        json.put("filename", filename.substring(0, filename.length() - 4));
        return json;
    }


    public static void main(String[] args) throws Exception {
        File pcmFile = new File("C:\\Users\\yzkj\\Desktop\\zth\\jetty-distribution-9.4.7.v20170914\\jetty-distribution-9.4.7.v20170914\\50.wav");
        BaiDu baiDu = new BaiDu();
        baiDu.getToken(pcmFile);
        System.out.println(baiDu.getResult1());
//        System.out.println(baiDu.getResult2());
    }
}

