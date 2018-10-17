package com.voiceai.baidu;

import android.content.Context;
import android.os.Environment;

import com.baidu.aip.speech.AipSpeech;
import com.voiceai.common.MateData;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class RestAPI {  //设置APPID/AK/SK
    public static final String APP_ID = "10674398";
    public static final String API_KEY = "a8aZUvtoQjsrsVKy7UolPtUe";
    public static final String SECRET_KEY = "d14094ef8273855e1736f6ddc7b487c0";

    public static String test() {
        // 初始化一个AipSpeech
        AipSpeech client = new AipSpeech(APP_ID, API_KEY, SECRET_KEY);

        // 可选：设置网络连接参数
        client.setConnectionTimeoutInMillis(6000);
        client.setSocketTimeoutInMillis(60000);


        // 调用接口
        JSONObject res = client.asr(Environment.getExternalStorageDirectory() + "/1535535642.pcm", "pcm", 16000, null);
        try {
            System.out.println(res.toString(2));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return res.toString();
    }

    public static RestAPI instance;
    private AipSpeech aipSpeech;

    public static RestAPI createInstance(Context context) {
        if (instance == null) {
            if (context == null) {
                return null;
            }
            instance = new RestAPI();
            instance.aipSpeech = new AipSpeech(MateData.readMetaDataFromApplication(context, "com.baidu.speech.APP_ID"),
                    MateData.readMetaDataFromApplication(context, "com.baidu.speech.API_KEY"),
                    MateData.readMetaDataFromApplication(context, "com.baidu.speech.SECRET_KEY"));
            // 可选：设置网络连接参数
            instance.aipSpeech.setConnectionTimeoutInMillis(6000);
            instance.aipSpeech.setSocketTimeoutInMillis(60000);
        }
        return instance;
    }

    public static interface Callback {
        void callback(String msg, Throwable err);
    }

    public void getText(String pcmpath, Callback callback) {
        new Thread(
                () -> {
                    // 调用接口
                    RestAPI apiSpeech = createInstance(null);
                    if (apiSpeech != null) {
                        JSONObject res = apiSpeech.aipSpeech.asr(pcmpath, "pcm", 16000, null);
                        try {
                            JSONArray results = res.getJSONArray("result");
                            if (results != null && results.length() > 0) {
                                String msg = results.getString(0);
                                callback.callback(msg, null);

                            } else {
                                callback.callback(null, new JSONException(res.toString()));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callback.callback(null, e);
                        }
                    } else {
                        callback.callback(null, new NullPointerException("尚未初始化"));
                    }
                }
        ).start();

    }

}
