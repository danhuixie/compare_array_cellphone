package com.voiceai.xunfei;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.sunflower.FlowerCollector;
import com.voiceai.asrexample.R;
import com.voiceai.common.JsonParser;
import com.voiceai.recorder.Recorder;
import com.voiceai.recorder.RecorderListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

public class IatDemo extends Activity implements OnClickListener {
    private static String TAG = IatDemo.class.getSimpleName();
    // 语音听写对象
    private SpeechRecognizer mIat;
    // 用HashMap存储听写结果
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    private Toast mToast;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;

    private boolean mTranslateEnable = false;
    private Toolbar mToorbar;
    /**
     * 麦克风阵列_VoiceAI2.0
     */
    private TextView mTitle1;
    /**
     * 阵列麦语音结果显示
     */
    private TextView mShowVoice;
    /**
     * 当前状态：
     */
    private TextView mStatueVoiceai;
    /**
     * 当前角度：
     */
    private TextView mAngle;
    /**
     * s
     */
    private TextView mAnglevalue;
    /**
     * 清除
     */
    private Button mClearVoiceai;
    /**
     * 设置
     */
    private Button mAngleset;
    /**
     * 手机麦_VoiceAIV2.0
     */
    private TextView mTitle2;
    /**
     * 手机麦结果显示
     */
    private TextView mShowBaidu;
    /**
     * 当前状态：
     */
    private TextView mStatueBaidu;
    /**
     * 清除
     */
    private Button mClearBaidu;
    /**
     * 开始录音
     */
    private Button mStart;
    /**
     * 录音
     */
    private Button mAnStart;
    /**
     * 停止
     */
    private Button mAnEnd;


    @SuppressLint("ShowToast")
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.demo);
        initView();
        mIat = SpeechRecognizer.createRecognizer(IatDemo.this, mInitListener);

        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);

        Recorder.getInstance(this).setRecorderListener(new RecorderListener() {
            @Override
            public void onRecordStart() {

            }

            @Override
            public void onAudioError(int errCode, String msg) {

            }

            @Override
            public void onVolumeChange(double volume) {

            }

            @Override
            public void onRecordEnd(String filePath) {

            }

            @Override
            public void onRecordFrame(byte[] data) {
                mIat.writeAudio(data, 0, data.length);
            }
        });
    }


    int ret = 0; // 函数调用返回值

    @Override
    public void onClick(View view) {
        if (null == mIat) {
            // 创建单例失败，与 21001 错误为同样原因，参考 http://bbs.xfyun.cn/forum.php?mod=viewthread&tid=9688
            this.showTip("创建对象失败，请确认 libmsc.so 放置正确，且有调用 createUtility 进行初始化");
            return;
        }

        switch (view.getId()) {

            case R.id.an_start:
                // 移动数据分析，收集开始听写事件
                FlowerCollector.onEvent(IatDemo.this, "iat_recognize");

                mIatResults.clear();
                // 设置参数
                setParam(mIat,false,SpeechConstant.TYPE_CLOUD);
                mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
                Recorder.getInstance(this).startRecorder();
                ret = mIat.startListening(mRecognizerListener);
                if (ret != ErrorCode.SUCCESS) {
                    showTip("听写失败,错误码：" + ret);
                } else {
                    showTip(getString(R.string.text_begin));

                }
                break;

            case R.id.an_end:
                mIat.stopListening();
                Recorder.getInstance(this).stopRecorder();
                showTip("停止听写");
                break;
        }
    }

    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码：" + code);
            }
        }
    };

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            showTip("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            if (mTranslateEnable && error.getErrorCode() == 14002) {
                showTip(error.getPlainDescription(true) + "\n请确认是否已开通翻译功能");
            } else {
                showTip(error.getPlainDescription(true));
            }
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
            showTip("结束说话");
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d(TAG, results.getResultString());
            if (mTranslateEnable) {
                printTransResult(results);
            } else {
                printResult(results);
            }

            if (isLast) {
                // TODO 最后的结果
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            showTip("当前正在说话，音量大小：" + volume);
            Log.d(TAG, "返回音频数据：" + data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            //	if (SpeechEvent.EVENT_SESSION_ID == eventType) {
            //		String sid = obj.getString(SpeechEvent.KEY_EVENT_SESSION_ID);
            //		Log.d(TAG, "session id =" + sid);
            //	}
        }
    };

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());

        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        mShowVoice.setText(resultBuffer.toString());

    }

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            if (mTranslateEnable) {
                printTransResult(results);
            } else {
                printResult(results);
            }

        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            if (mTranslateEnable && error.getErrorCode() == 14002) {
                showTip(error.getPlainDescription(true) + "\n请确认是否已开通翻译功能");
            } else {
                showTip(error.getPlainDescription(true));
            }
        }

    };


    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }

    /**
     * 参数设置
     *
     * @return
     */
    public static void setParam(SpeechRecognizer mIat, boolean mTranslateEnable,String mEngineType) {
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");


        if (mTranslateEnable) {
            Log.i(TAG, "translate enable");
            mIat.setParameter(SpeechConstant.ASR_SCH, "1");
            mIat.setParameter(SpeechConstant.ADD_CAP, "translate");
            mIat.setParameter(SpeechConstant.TRS_SRC, "its");
        }
        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");

        if (mTranslateEnable) {
            mIat.setParameter(SpeechConstant.ORI_LANG, "cn");
            mIat.setParameter(SpeechConstant.TRANS_LANG, "en");
        }
        //此处用于设置dialog中不显示错误码信息
        //mIat.setParameter("view_tips_plain","false");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, 400000 + "");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, 100000 + "");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
//        mIat.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory() + "/msc/iat.wav");
    }

    private void printTransResult(RecognizerResult results) {
        String trans = JsonParser.parseTransResult(results.getResultString(), "dst");
        String oris = JsonParser.parseTransResult(results.getResultString(), "src");

        if (TextUtils.isEmpty(trans) || TextUtils.isEmpty(oris)) {
            showTip("解析结果失败，请确认是否已开通翻译功能。");
        } else {
            mShowVoice.setText("原始语言:\n" + oris + "\n目标语言:\n" + trans);
        }

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (null != mIat) {
            // 退出时释放连接
            mIat.cancel();
            mIat.destroy();
        }
    }

    @Override
    protected void onResume() {
        // 开放统计 移动数据统计分析
        FlowerCollector.onResume(IatDemo.this);
        FlowerCollector.onPageStart(TAG);
        super.onResume();
    }

    @Override
    protected void onPause() {
        // 开放统计 移动数据统计分析
        FlowerCollector.onPageEnd(TAG);
        FlowerCollector.onPause(IatDemo.this);
        super.onPause();
    }

    private void initView() {
        mToorbar = findViewById(R.id.toorbar);
        mTitle1 = findViewById(R.id.title1);
        mShowVoice = findViewById(R.id.show_voice);
        mStatueVoiceai = findViewById(R.id.statue_voiceai);
        mAngle = findViewById(R.id.angle);
        mAnglevalue = findViewById(R.id.anglevalue);
        mClearVoiceai = findViewById(R.id.clear_voiceai);
        mAngleset = findViewById(R.id.angleset);
        mTitle2 = findViewById(R.id.title2);
        mShowBaidu = findViewById(R.id.show_baidu);
        mStatueBaidu = findViewById(R.id.statue_baidu);
        mClearBaidu = findViewById(R.id.clear_baidu);
        mStart = findViewById(R.id.start);
        mAnStart = findViewById(R.id.an_start);
        mAnStart.setOnClickListener(this);
        mAnEnd = findViewById(R.id.an_end);
        mAnEnd.setOnClickListener(this);
    }
}
