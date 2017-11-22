//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package controller;

import com.iflytek.cloud.speech.RecognizerListener;
import com.iflytek.cloud.speech.RecognizerResult;
import com.iflytek.cloud.speech.Setting;
import com.iflytek.cloud.speech.SpeechError;
import com.iflytek.cloud.speech.SpeechRecognizer;
import com.iflytek.cloud.speech.SpeechUtility;
//import com.iflytek.util.DebugLog;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;

public class xf {
    private static final String APPID = "58f04ef9";
    private StringBuffer mResult = new StringBuffer();
    private int maxWaitTime = 500;
    private int perWaitTime = 100;
    private int maxQueueTimes = 3;
    private String fileName = "";
    private RecognizerListener recListener = new RecognizerListener() {
        public void onBeginOfSpeech() {
        }

        public void onEndOfSpeech() {
        }

        public void onVolumeChanged(int volume) {
        }

        public void onResult(RecognizerResult result, boolean islast) {
            xf.this.mResult.append(result.getResultString());
            System.out.println(result.getResultString());
//            DebugLog.Log(result.getResultString());
        }

        public void onError(SpeechError error) {
            System.out.println(error.getErrorDescription(true));

            try {
                xf.this.voice2words(xf.this.fileName);
                xf.this.maxQueueTimes = xf.this.maxQueueTimes - 1;
            } catch (InterruptedException var3) {
                Thread.currentThread().interrupt();
                throw new RuntimeException(var3);
            }
        }

        public void onEvent(int eventType, int arg1, int agr2, String msg) {
        }
    };

    static {
        Setting.setShowLog(false);
        SpeechUtility.createUtility("appid=59f855ee");
    }

    public xf() {
    }

    public String voice2words(String fileName) throws InterruptedException {
        return this.voice2words(fileName, true);
    }

    public String voice2words(String fileName, boolean init) throws InterruptedException {
        if (init) {
            this.maxWaitTime = 500;
            this.maxQueueTimes = 3;
        }

        if (this.maxQueueTimes <= 0) {
            this.mResult.setLength(0);
            this.mResult.append("解析异常！");
            return this.mResult.toString();
        } else {
            this.fileName = fileName;
            return this.recognize();
        }
    }

    private String recognize() throws InterruptedException {
        if (SpeechRecognizer.getRecognizer() == null) {
            SpeechRecognizer.createRecognizer();
        }

        return this.RecognizePcmfileByte();
    }

    private String RecognizePcmfileByte() throws InterruptedException {
        FileInputStream fis = null;
        byte[] voiceBuffer = null;

        try {
            fis = new FileInputStream(new File(this.fileName));
            voiceBuffer = new byte[fis.available()];
            fis.read(voiceBuffer);
        } catch (Exception var15) {
            var15.printStackTrace();
        } finally {
            try {
                if (fis != null) {
                    fis.close();
                    fis = null;
                }
            } catch (IOException var13) {
                var13.printStackTrace();
            }

        }

        if (voiceBuffer.length == 0) {
            this.mResult.append("no audio avaible!");
        } else {
            this.mResult.setLength(0);
            SpeechRecognizer recognizer = SpeechRecognizer.getRecognizer();
            recognizer.setParameter("domain", "iat");
            recognizer.setParameter("language", "zh_cn");
            recognizer.setParameter("audio_source", "-1");
            recognizer.setParameter("result_type", "plain");
            recognizer.setParameter("speech_timeout", "60000");
            recognizer.setParameter("vad_eos", "10000");
            recognizer.startListening(this.recListener);
            ArrayList<byte[]> buffers = this.splitBuffer(voiceBuffer, voiceBuffer.length, 4800);
            System.out.println("Buffers length:" + buffers.size());

            for(int i = 0; i < buffers.size(); ++i) {
                recognizer.writeAudio((byte[])buffers.get(i), 0, ((byte[])buffers.get(i)).length);

                try {
                    Thread.sleep(150L);
                    System.out.println("写入");
                } catch (InterruptedException var14) {
                    var14.printStackTrace();
                }
            }

            recognizer.stopListening();
        }

        return this.mResult.toString();
    }

    private ArrayList<byte[]> splitBuffer(byte[] buffer, int length, int spsize) {
        ArrayList<byte[]> array = new ArrayList();
        if (spsize > 0 && length > 0 && buffer != null && buffer.length >= length) {
            int size = 0;

            while(size < length) {
                int left = length - size;
                byte[] sdata;
                if (spsize < left) {
                    sdata = new byte[spsize];
                    System.arraycopy(buffer, size, sdata, 0, spsize);
                    array.add(sdata);
                    size += spsize;
                } else {
                    sdata = new byte[left];
                    System.arraycopy(buffer, size, sdata, 0, left);
                    array.add(sdata);
                    size += left;
                }
            }

            return array;
        } else {
            return array;
        }
    }

    public static void main(String[] args) throws InterruptedException {
        xf xf=new xf();
        xf.voice2words("C:\\Users\\59785\\Desktop\\test\\nibupanilaopozhidaoa.wav");
        Thread.sleep(1000);
        xf.voice2words("C:\\Users\\59785\\Desktop\\test\\test3.pcm");
        Thread.sleep(1000);
        xf.voice2words("C:\\Users\\59785\\Desktop\\test\\09.wav");
    }
}
