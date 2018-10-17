package com.voiceai.vprcjavasdk;

import android.widget.TextView;

import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

public interface ASRSDK {

   public static final String MODEL_ASR_POWER="model_asr_power";
    public static final String MODEL_ASR_NUMBER="model_asr_number";

    void onDestory();

    void start(VPRCSDK vprcsdk, String modeltype,int src_sample_rate, Response response);

    void setSendByteListener(Listener listener);

    void send(byte[] bytes);

    void sendfinal();

    public static void dealTextView(TextView textView, String str2, boolean boo) {
        if (str2.contains("CRPV")) {
            byte[] k = new byte[str2.substring(str2.indexOf("CRPV")).getBytes().length - 43];
            System.arraycopy(str2.substring(str2.indexOf("CRPV")).getBytes(), 43, k, 0, k.length);
            str2 = new String(k);
            boo=true;
        }


        String str1 = textView.getText().toString();
        if (boo) {
            str2 = str2 + "\n";
        }
        String reg = "\n";
        if ((str1).endsWith(reg)) {
            str1 = str1 + str2;
        } else {
            String[] strs = str1.split("\n");
            String re = "";
            for (int i = 0; i < strs.length - 1; i++)
                re = re + strs[i] + "\n";
            str1 = re + str2;
        }
        textView.setText(str1);
    }

    public static byte[] head(boolean isfinal, int length) {
        byte[] header = new byte[32];
        header[0] = 'C'; // RIFF/WAVE header
        header[1] = 'R';
        header[2] = 'P';
        header[3] = 'V';
        header[4] = 1;
        header[5] = 0;
        header[6] = 0;
        header[7] = 0;
        header[8] = 1;
        header[9] = 0;
        header[10] = 1;
        header[11] = (byte) (isfinal ? 1 : 0);
        int byteRate = length + 16;
        header[12] = (byte) (byteRate & 0xff);
        header[13] = (byte) ((byteRate >> 8) & 0xff);
        header[14] = (byte) ((byteRate >> 16) & 0xff);
        header[15] = (byte) ((byteRate >> 24) & 0xff);
        header[16] = 'C'; // RIFF/WAVE header
        header[17] = 'R';
        header[18] = 'P';
        header[19] = 'V';
        header[20] = 16; // 4 bytes: size of 'fmt ' chunk
        header[21] = 0;
        header[22] = 0;
        header[23] = 0;
        header[24] = (byte) (isfinal ? 1 : 0);
        header[25] = 0;
        header[26] = 0;
        header[27] = 0;
        header[28] = 0;
        header[29] = 0;
        header[30] = 0;
        header[31] = 0;
        return header;

    }

    public static String getWebUrl(String baseurl) {
        if (baseurl.contains("https://")) return baseurl.replaceAll("https://", "wss://");
        else if (baseurl.contains("http://")) return baseurl.replaceAll("http://", "ws://");
        else return "ws://" + baseurl;
    }

    public static interface Listener {
        void accept(String msg, boolean isfinal, Throwable err);
    }
}
