package controller;

import org.json.JSONObject;
import org.springframework.stereotype.Component;
import javax.xml.bind.DatatypeConverter;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;



@Component
public class BaiDu {

    private static final String serverURL = "http://vop.baidu.com/server_api";
    private static String token = "";//put your own params here
    private static final String apiKey = "BD4l7gz5xGAK5DlMFQokorhO";
    private static final String secretKey = "ab26f77fe85788ccd92707b1c0f5a71d";
    private static final String cuid = "54-04-A6-77-E0-84";
    private String result1=" ";
    private long btime;
    private long et;
    private long time;

    public static void main(String[] args) throws Exception {
        FileWriter file =new FileWriter("result.txt");
        BufferedWriter bw = new BufferedWriter(file);
        bw.write("ni hao a ni hao a ");
        bw.flush();
        bw.close();

    }

    public void getToken(File file) throws Exception {
        btime = System.currentTimeMillis();
        String getTokenURL = "https://openapi.baidu.com/oauth/2.0/token?grant_type=client_credentials" +
                "&client_id=" + apiKey + "&client_secret=" + secretKey;
        HttpURLConnection conn = (HttpURLConnection) new URL(getTokenURL).openConnection();
        token = new JSONObject(printResponse(conn)).getString("access_token");
        String filetype=file.getAbsolutePath().substring(file.getAbsolutePath().length()-3);
        System.out.println(filetype);
        method1(file,filetype);
    }

    public void method1(File file,String filetype) throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(serverURL).openConnection();

        // construct params
        JSONObject params = new JSONObject();
        params.put("format", filetype);
        params.put("rate", 16000);
        params.put("channel", "1");
        params.put("token", token);
        params.put("cuid", cuid);
        params.put("len", file.length());
        params.put("speech", DatatypeConverter.printBase64Binary(loadFile(file)));

        // add request header
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8");

        conn.setDoInput(true);
        conn.setDoOutput(true);

        // send request
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.writeBytes(params.toString());
        wr.flush();
        wr.close();

        JSONObject jsonObject=new JSONObject(printResponse(conn));
        try{
            result1=jsonObject.getJSONArray("result").getString(0);
            byte b[]=result1.getBytes();
            for(int i=0;i<b.length;i++){
                if(b[i]==63){
                    b[i]=-128;
                }
            }
            result1=new String(b,"utf-8");
            result1=result1.substring(0,result1.length()-1);
            et = System.currentTimeMillis();
            time = et-btime;
            System.out.println(result1);
        }
        catch (Exception e){
            result1 = "音频质量过差";
            e.printStackTrace();
        }
    }

    public String printResponse(HttpURLConnection conn) throws Exception {
        if (conn.getResponseCode() != 200) {
            // request error
            return "";
        }
        InputStream is = conn.getInputStream();
        BufferedReader rd = new BufferedReader(new InputStreamReader(is));
        String line;
        StringBuffer response = new StringBuffer();
        while ((line = rd.readLine()) != null) {
            response.append(line);
            response.append('\r');
        }
        rd.close();
        System.out.println(new JSONObject(response.toString()).toString(4));
        return response.toString();
    }

    public byte[] loadFile(File file) throws IOException {
        InputStream is = new FileInputStream(file);

        long length = file.length();
        byte[] bytes = new byte[(int) length];

        int offset = 0;
        int numRead = 0;
        while (offset < bytes.length
                && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
            offset += numRead;
        }

        if (offset < bytes.length) {
            is.close();
            throw new IOException("Could not completely read file " + file.getName());
        }

        is.close();
        return bytes;
    }
    public String getResult1(){
        return result1;
    }

    public long getTime() {
        return time;
    }

}
