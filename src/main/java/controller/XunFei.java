package controller;

import com.iflytek.cloud.speech.*;
import org.springframework.stereotype.Component;

import java.io.*;
import java.util.ArrayList;
@Component
public class XunFei {
	private static final String APPID = "59f855ee";
	private StringBuffer mResult = new StringBuffer();
	private int maxWaitTime = 5000;
	private int perWaitTime = 100;
	private int maxQueueTimes = 3;
	private String result = "";
	private long bt;
	private long et;
	private long time;
	public String  error1;
	static {
		Setting.setShowLog(false);
		SpeechUtility.createUtility("appid=" + APPID);
	}
	public void RecognizePcmfileByte(InputStream fis) throws InterruptedException {
//		FileInputStream fis = null;
		bt = System.currentTimeMillis();
        SpeechRecognizer.createRecognizer();
		byte[] voiceBuffer = null;
		try {
//			fis = new FileInputStream(new File(fileName));
			voiceBuffer = new byte[fis.available()];
			System.out.println(voiceBuffer.length);
			fis.read(voiceBuffer);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				if (null != fis) {
					fis.close();
					fis = null;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		if (0 == voiceBuffer.length) {
			mResult.append("no audio avaible!");
		} else {
			mResult.setLength(0);
			SpeechRecognizer recognizer = SpeechRecognizer.getRecognizer();
			recognizer.setParameter(SpeechConstant.DOMAIN, "iat");
			recognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
			recognizer.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
			recognizer.setParameter( SpeechConstant.RESULT_TYPE, "plain" );
			recognizer.setParameter(SpeechConstant.KEY_SPEECH_TIMEOUT, "100000");
			recognizer.setParameter(SpeechConstant.ASR_PTT, "0");
			maxWaitTime = 5000;


			recognizer.startListening(recListener);
			ArrayList<byte[]> buffers = splitBuffer(voiceBuffer,
					voiceBuffer.length, 4800);
			System.out.println("Buffers length:" + buffers.size());
			for (int i = 0; i < buffers.size(); i++) {
				recognizer.writeAudio(buffers.get(i), 0, buffers.get(i).length);
				System.out.println(buffers.get(i).length);
//				try {
//					Thread.sleep(150);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
			}
			recognizer.stopListening();

				while(recognizer.isListening()) {
				if(maxWaitTime < 0) {
					mResult.setLength(0);
					mResult.append("超时");
					break;
				}
				Thread.sleep(perWaitTime);
				System.out.println("wait");
				maxWaitTime -= perWaitTime;
			}
		}
		et = System.currentTimeMillis();
		time= et -bt;
		result = mResult.toString();
	}

	private ArrayList<byte[]> splitBuffer(byte[] buffer, int length, int spsize) {
		ArrayList<byte[]> array = new ArrayList<byte[]>();
		if (spsize <= 0 || length <= 0 || buffer == null
				|| buffer.length < length)
			return array;
		int size = 0;
		while (size < length) {
			int left = length - size;
			if (spsize < left) {
				byte[] sdata = new byte[spsize];
				System.arraycopy(buffer, size, sdata, 0, spsize);
				array.add(sdata);
				size += spsize;
			} else {
				byte[] sdata = new byte[left];
				System.arraycopy(buffer, size, sdata, 0, left);
				array.add(sdata);
				size += left;
			}
		}

		return array;

	}

	private RecognizerListener recListener = new RecognizerListener() {

		public void onBeginOfSpeech() { }

		public void onEndOfSpeech() { }

		public void onVolumeChanged(int volume) { }

		public void onResult(RecognizerResult result, boolean islast) {
			mResult.append(result.getResultString());
			System.out.println(result.getResultString());
		}

		public void onError(SpeechError error) {
			error1=error.getErrorDescription(true);
			System.out.println(error.getErrorDescription(true));
//			try {
//				voice2words(fileName);
//				maxQueueTimes--;
//			} catch (InterruptedException e) {
//				Thread.currentThread().interrupt();
//				throw new RuntimeException(e);
//			}
		}

		public void onEvent(int eventType, int arg1, int agr2, String msg) { }

	};

	public String getResult() {
		return result;
	}

	public long getTime() {
		return time;
	}

	public static void main(String[] args) throws InterruptedException, FileNotFoundException {
		XunFei xunFei = new XunFei();
		File file = new File("C:\\Users\\yzkj\\Desktop\\zth\\jetty-distribution-9.4.7.v20170914\\jetty-distribution-9.4.7.v20170914\\50.wav");
		InputStream fis = new FileInputStream(file);
		xunFei.RecognizePcmfileByte(fis);
		System.out.println(xunFei.getResult());
	}

}
