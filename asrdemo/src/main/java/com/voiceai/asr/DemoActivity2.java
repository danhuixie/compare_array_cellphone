package com.voiceai.asr;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.aip.asrwakeup3.core.mini.AutoCheck;
import com.baidu.aip.asrwakeup3.core.recog.MyRecognizer;
import com.baidu.aip.asrwakeup3.core.recog.listener.IRecogListener;
import com.baidu.aip.speech.AipSpeech;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.Setting;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.sunflower.FlowerCollector;
import com.jiyun.api.JiyunMicArrayImplement;
import com.jiyun.api.JiyunMicArrayInterface;
import com.jiyun.api.MReadThread;
import com.voiceai.BaiduInputStream;
import com.voiceai.SpeechApp;
import com.voiceai.asrexample.R;
import com.voiceai.baidu.CommonRecogParams;
import com.voiceai.baidu.MyMessageStatusRecogListener;
import com.voiceai.baidu.RestAPI;
import com.voiceai.common.EasyPermissionDemoActivity;
import com.voiceai.common.JsonParser;
import com.voiceai.common.MoneyInputFilter;
import com.voiceai.recorder.Recorder;
import com.voiceai.recorder.RecorderImpl;
import com.voiceai.recorder.RecorderListener;
import com.voiceai.vprcjavasdk.ASRSDK;
import com.voiceai.vprcjavasdk.Config;
import com.voiceai.vprcjavasdk.MWebSocketClient;
import com.voiceai.vprcjavasdk.VPRCNetImpl;
import com.voiceai.vprcjavasdk.VPRCSDK;
import com.voiceai.vprcjavasdk.VPRCSDKImpl;
import com.voiceai.xunfei.IatDemo;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.voiceai.xunfei.IatDemo.setParam;

public class DemoActivity2 extends EasyPermissionDemoActivity {

    private Toolbar mToorbar;
    private TextView mTitle1;
    private TextView mShowVoice;
    private TextView mStatueVoiceai;
    private Button mClearVoiceai;
    private TextView mTitle2;
    private TextView mShow2;
    private TextView mStatue2;
    private Button mClear2;
    private Button mStart;
    private Button mAnStart;
    private Button mAnEnd;
    private TextView mAngle;
    private TextView mAnglevalue;
    private Button mAngleset;
    private View sure2;
    private EditText edit;
    private View cancel2;
    private MyRecognizer baidusdk;
    private boolean isrecorder;
    private SpeechRecognizer mIat;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo);
        initView();
        initmy();
        mToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
//        new Thread(()->{
//          String re=  RestAPI.test();
//          runOnUiThread(()->{
//              mShow2.setText(re);
//          });
//        }).start();


    }

    private void initmy() {
        if (i == 1)
            changeView();
        i++;
    }

    int i;

    @Override
    public void next() {
        super.next();
        initmy();
    }

    public int mode = 1;
    public final static int MODE_VOICEAI = 0;
    public final static int MODE_Baidu = 1;
    public final static int MODE_Xunfei = 2;

    private void initsdk() {
        if (mode == MODE_VOICEAI) {
            initVoiceAI();
        } else if (mode == MODE_Xunfei) {
            initXunfei();
        } else {
            initBaidu();
        }
        initRecorder();
    }

    private void showTip(final String str) {
//        mToast.setText(str);
//        mToast.show();
        Toast.makeText(this, str, Toast.LENGTH_SHORT).show();
    }

    /**
     * xunfei 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d("demoactivity", "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码：" + code);
            }
        }
    };

    private void initXunfei() {
        SpeechUtility.createUtility(this, "appid=" + "5bc4382e");
        Setting.setShowLog(true);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                mIat = SpeechRecognizer.createRecognizer(DemoActivity2.this, mInitListener);
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        destroy();

    }

    private void destroy() {
        if (asrsdk1 != null) {
            asrsdk1.onDestory();
            asrsdk1 = null;
        }
        if (asrsdk2 != null) {
            asrsdk2.onDestory();
            asrsdk2 = null;
        }
        if (jiyun != null) {
            jiyun.stopCapturing();
            jiyun.release();
            jiyun = null;
        }
        if (mReadThread != null) {
            mReadThread.cancel();
            mReadThread.interrupt();
            mReadThread = null;
        }
        if (baidusdk != null) {
            try {
                baidusdk.stop();
                baidusdk.release();
                baidusdk = null;
            } catch (Exception e) {
            }
        }
        if (null != mIat) {
            // 退出时释放连接
            mIat.cancel();
            mIat.destroy();
            mIat = null;
        }

        RecorderImpl.getInstance(this).onDestory();
    }

    MReadThread mReadThread;
    private HashMap<String, String> mIatResults = new LinkedHashMap<String, String>();

    private void startRecorder() {
        if (jiyun != null) {
            jiyun.startCapturing();
        }
        if (mReadThread != null) {
            mReadThread.cancel();
            mReadThread.interrupt();
        }
        if (jiyun != null) {
            mReadThread = new MReadThread(jiyun, new com.jiyun.api.Listener() {
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
                    if (mode == MODE_VOICEAI && asrsdk1 != null) {
                        asrsdk1.sendfinal();
                    } else if (mode == MODE_Baidu) {
                        baidusdk.stop();
//                        baidusdk.cancel();
                        if (isrecorder) {
                            new Thread(() -> {
                                try {
                                    Thread.sleep(2500);
                                    runOnUiThread(() -> {
                                        startBaidu();
                                    });
                                } catch (Exception e) {
                                }
                            }).start();
                        }

                    } else if (mode == MODE_Xunfei&&jiyun!=null) {
                        mIat.stopListening();
                        new Thread(() -> {
                            while (mIat.isListening()) {
                                try {
                                    Thread.sleep(100);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                            }
                            runOnUiThread(() -> {
                                if (isrecorder)
                                    startXunfei();
                            });
                        }).start();
                    }

                }

                @Override
                public void onRecordFrame(byte[] data) {
                    if (mode == MODE_VOICEAI && asrsdk1 != null) {
                        asrsdk1.send(data);
                    } else if (mode == MODE_Baidu) {
                        BaiduInputStream.getInstance().push(data);
                    } else if (mode == MODE_Xunfei) {
                        if (mIat.isListening()) {
                            mIat.writeAudio(data, 0, data.length);
                        }
                    }
                }

                @Override
                public void onAnglechange(int data) {
                    mAnglevalue.setText("" + data);
                }
            });
            mReadThread.start();
        }
        RecorderImpl.getInstance(this).startRecorder();
        if (mode == MODE_Baidu) {
            startBaidu();
        } else if (mode == MODE_Xunfei) {
            startXunfei();
        }
        isrecorder = true;
    }

    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
//            showTip("当前正在说话，音量大小：" + volume);
//            Log.d("demoactivity", "返回音频数据：" + data.length);
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

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
//            showTip("开始说话");
            if (jiyun == null) {
                mStatue2.setText("开始说话");
            } else {
                mStatueVoiceai.setText("开始说话");
            }
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
            if (error.getErrorCode() == 10118) {
                return;
            }
            showTip(error.getPlainDescription(true));

        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
//            showTip("结束说话");
            if (jiyun == null) {
                mStatue2.setText("结束说话");
            } else {
                mStatueVoiceai.setText("结束说话");
            }
        }

        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.d("demoactivity", results.getResultString());
            printResult(results, isLast);
            if (isLast) {
                // TODO 最后的结果
            }
        }
    };

    private void printResult(RecognizerResult results, boolean isLast) {
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
        if (jiyun == null) {
            ASRSDK.dealTextView(mShow2, resultBuffer.toString(), isLast);
            mStatue2.setText("正在说话");
        } else {
            ASRSDK.dealTextView(mShowVoice, resultBuffer.toString(), isLast);
            mStatueVoiceai.setText("正在说话");
        }


    }

    private void stopRecorder() {
        Log.e("DemoActivity", "stopRecorder" + System.currentTimeMillis() + "");
        if (jiyun != null) {
            Log.e("DemoActivity", "jiyun.stopCapturing" + System.currentTimeMillis() + "");
            jiyun.stopCapturing();
            Log.e("DemoActivity", "" + System.currentTimeMillis() + "jiyun.stopCapturing");
        }
        if (mReadThread != null) {
            mReadThread.cancel();
            mReadThread.interrupt();
            mReadThread = null;

        }
        Log.e("DemoActivity", "" + System.currentTimeMillis() + "1");
        RecorderImpl.getInstance(this).stopRecorder();
        if (asrsdk1 != null) {
            asrsdk1.sendfinal();
        }
        Log.e("DemoActivity", "" + System.currentTimeMillis() + "2");
        if (asrsdk2 != null) {
            asrsdk2.sendfinal();
        }
        Log.e("DemoActivity", "" + System.currentTimeMillis() + "3");
        if (baidusdk != null && mode == MODE_Baidu) {
            baidusdk.stop();
        }
        if (mIat != null) {
            mIat.stopListening();
        }
        Log.e("DemoActivity", "" + System.currentTimeMillis() + "stopRecorder");
        isrecorder = false;

    }

    public static void startBaidu(Context context, MyRecognizer myRecognizer) {
        // DEMO集成步骤2.1 拼接识别参数： 此处params可以打印出来，直接写到你的代码里去，最终的json一致即可。
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("vad", "touch,  关闭静音断句功能。用戶手动停止录音").commit();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("infile", "#com.voiceai.BaiduInputStream.getInstance(), 自行从队列读取").commit();
        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("vad.endpoint-timeout", "0, 开启长语音（离线不支持）。建议pid选择15362。").commit();
        final Map<String, Object> params = new CommonRecogParams().fetch(PreferenceManager.getDefaultSharedPreferences(context));
        (new AutoCheck(context.getApplicationContext(), new Handler() {
            public void handleMessage(Message msg) {
                if (msg.what == 100) {
                    AutoCheck autoCheck = (AutoCheck) msg.obj;
                    synchronized (autoCheck) {
                        String message = autoCheck.obtainErrorMessage(); // autoCheck.obtainAllMessage();
//                        txtLog.append(message + "\n");
                        ; // 可以用下面一行替代，在logcat中查看代码
                        Log.w("AutoCheckMessage", message);
                    }
                }
            }
        }, false)).checkAsr(params);
        // 这里打印出params， 填写至您自己的app中，直接调用下面这行代码即可。
        // DEMO集成步骤2.2 开始识别
        printMap(params);
        myRecognizer.start(params);
    }

    static void printMap(Map<String, Object> map) {
//        map.forEach((str, ob) -> {
//            if (str != null && ob != null)
//                Log.w("AutoCheckMessage", str + "obj:" + ob.toString());
//        });
        Iterator<String> a = map.keySet().iterator();
        while (a.hasNext()) {
            String str = a.next();
            if (str != null && map.get(str) != null)
                Log.w("AutoCheckMessage", str + "obj:" + map.get(str).toString());
        }
    }

    private void initBaidu() {
        //todo....
        if (baidusdk == null) {
            IRecogListener listener = new MyMessageStatusRecogListener(new Handler(), mShowVoice, mStatueVoiceai);
            baidusdk = new MyRecognizer(this, listener);
        }
        if (baidusdk2 == null) {
            baidusdk2 = RestAPI.createInstance(this);
        }
    }

    RestAPI baidusdk2;

    public void startBaidu() {
        startBaidu(this, baidusdk);
    }

    private Toast mToast;

    int ret = 0;

    public void startXunfei() {
        // 移动数据分析，收集开始听写事件
        Log.e("demoactivity", "讯飞开始了");
        FlowerCollector.onEvent(DemoActivity2.this, "iat_recognize");
        mIatResults.clear();
        // 设置参数
        setParam(mIat, false, SpeechConstant.TYPE_CLOUD);
        mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
        ret = mIat.startListening(mRecognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            showTip("听写失败,错误码：" + ret);
        } else {
//            showTip(getString(R.string.text_begin));
        }
    }


    ASRSDK asrsdk1;
    ASRSDK asrsdk2;

    private void initVoiceAI() {
        if (asrsdk1 == null) {
            Config config = new Config(this);
            VPRCSDK v = new VPRCSDKImpl(new VPRCNetImpl());
            asrsdk1 = new MWebSocketClient();
            v.init(this, config, (res, e) -> {
                if (e == null) {
                    if (mStatueVoiceai != null) {
                        mStatueVoiceai.setText("VPRC1初始化完成");
                        try {
                            asrsdk1.start(v, ASRSDK.MODEL_ASR_POWER, 16000, (resc, err) -> {
                                if (err == null) {
                                    mStatueVoiceai.setText("ASR1初始化完成");
                                    asrsdk1.setSendByteListener(new ASRSDK.Listener() {
                                        @Override
                                        public void accept(String msg, boolean isfinal, Throwable err) {
                                            if (err == null) {
                                                if (isfinal) {
                                                    mStatueVoiceai.setText("说话结束");
                                                } else
                                                    mStatueVoiceai.setText("说话中");
                                                ASRSDK.dealTextView(mShowVoice, msg, isfinal);
                                            } else {
                                                if (mStatueVoiceai != null) {
                                                    mStatueVoiceai.setText("ASR1异常" + err.getMessage());
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    if (mStatueVoiceai != null) {
                                        mStatueVoiceai.setText("网络异常" + (e != null ? e.getMessage() : ""));
                                    }
                                }
                            });
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                } else {
                    if (mStatueVoiceai != null) {
                        mStatueVoiceai.setText("VPRC1初始化异常" + e.getMessage());
                    }
                }
            });
        }
        if (asrsdk2 == null) {
            Config config = new Config(this);
            VPRCSDK v = new VPRCSDKImpl(new VPRCNetImpl());
            asrsdk2 = new MWebSocketClient();
            v.init(this, config, (res, e) -> {
                if (e == null) {
                    if (mStatue2 != null) {
                        mStatue2.setText("VPRC1初始化完成");
                        try {
                            asrsdk2.start(v, ASRSDK.MODEL_ASR_POWER, 16000, (resc, err) -> {
                                if (err == null) {
                                    mStatue2.setText("ASR1初始化完成");
                                    asrsdk2.setSendByteListener(new ASRSDK.Listener() {
                                        @Override
                                        public void accept(String msg, boolean isfinal, Throwable err) {
                                            if (err == null) {
                                                if (isfinal) {
                                                    mStatue2.setText("说话结束");
                                                } else
                                                    mStatue2.setText("说话中");
                                                ASRSDK.dealTextView(mShow2, msg, isfinal);
                                            } else {
                                                if (mStatue2 != null) {
                                                    mStatue2.setText("ASR1异常" + err.getMessage());
                                                }
                                            }
                                        }
                                    });
                                } else {
                                    if (mStatue2 != null) {
                                        mStatue2.setText("网络异常" + (e != null ? e.getMessage() : ""));
                                    }
                                }
                            });
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }
                    }
                } else {
                    if (mStatue2 != null) {
                        mStatue2.setText("VPRC1初始化异常" + e.getMessage());
                    }
                }
            });
        }

    }

    JiyunMicArrayInterface jiyun;
    Recorder recorder;


    private void initRecorder() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Process process = null;
                try {

                    String cmdr = "chmod 777 " + getPackageCodePath();
                    process = Runtime.getRuntime().exec("su"); //切换到root帐号
                    DataOutputStream os2 = new DataOutputStream(process.getOutputStream());
                    os2.writeBytes(cmdr + "\n");
                    os2.writeBytes("exit\n");
                    os2.flush();
                    if (process.waitFor() == 0) {
                        runOnUiThread(() -> {
                            Toast.makeText(DemoActivity2.this, "root请求授权成功", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(DemoActivity2.this, "root授权失败，阵列麦克风将不会工作", Toast.LENGTH_SHORT).show();
                        });
                    }
                    System.out.println("spicax赋权完成一半:" + getPackageCodePath() + process.waitFor());
//                    process = Runtime.getRuntime().exec("su"); //切换到root帐号
//                    OutputStream out = process.getOutputStream();
//                    out.write((("chmod 777 /dev/snd/pcmC1D0c")).getBytes());
//                    out.flush();
//                    out.close();
                    process = Runtime.getRuntime().exec("su"); //切换到root帐号
                    String cmd = "chmod 777 /dev/snd/pcmC1D0c\nsetenforce 0\n";
                    DataOutputStream os = new DataOutputStream(process.getOutputStream());
                    os.writeBytes(cmd + "\n");
                    os.writeBytes("exit\n");
                    os.flush();
                    os.close();


//                    process = Runtime.getRuntime().exec("su");
//                    int iPid = getProcessId(process.toString());
//                    dataOutputStream = new DataOutputStream(process.getOutputStream());
//                    dataInputStream = new DataInputStream(process.getInputStream());
//                    dataOutputStream.writeBytes("cat /data/misc/wifi/*.conf\n");
//                    dataOutputStream.writeBytes("exit\n");
//                    dataOutputStream.flush();
//                    InputStreamReader inputStreamReader = new InputStreamReader(
//                            dataInputStream, "UTF-8");
//                    BufferedReader bufferedReader = new BufferedReader(
//                            inputStreamReader);
//                    String line = null;
//                    while ((line = bufferedReader.readLine()) != null) {
//                        wifiConf.append(line);
//                    }
//                    bufferedReader.close();
//                    inputStreamReader.close();
//                    process.waitFor();

                    System.out.println("spicax赋权完成+waitfor:" + process.waitFor());


                    String path = "/AAAA";
                    File file = new File(Environment.getExternalStorageDirectory().getPath() + path);
                    if (!file.exists()) {
                        file.mkdirs();
                    }
                    jiyun = new JiyunMicArrayImplement();
                    boolean a = jiyun.initialize("", "", file.getPath());
                    if (a) {
                        runOnUiThread(() -> {
                            Toast.makeText(DemoActivity2.this, "阵列麦克风初始化成功", Toast.LENGTH_SHORT).show();
                        });
                    } else {
                        runOnUiThread(() -> {
                            Toast.makeText(DemoActivity2.this, "阵列麦克风初始化失败", Toast.LENGTH_SHORT).show();
                            jiyun = null;
                        });
                    }
                    System.out.println("demoactivity" + a);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
        recorder = RecorderImpl.getInstance(this);
        recorder.setRecorderListener(new RecorderListener() {
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
                Log.e("demoactivity", "暂停");
                if (mode == MODE_VOICEAI && asrsdk2 != null) {
                    asrsdk2.sendfinal();
                } else if (mode == MODE_Baidu) {
                    baidusdk2.getText(filePath.replaceAll("\\.wav", ".pcm"), (res, err) -> {
                        if (err == null) {
                            mShow2.post(() -> ASRSDK.dealTextView(mShow2, res, true));
                        } else {
                            mStatue2.post(() -> mStatue2.setText(err.getMessage()));
                        }
                    });
                } else if (mode == MODE_Xunfei&&jiyun==null) {
                    mIat.stopListening();
                    new Thread(() -> {
                        while (mIat.isListening()) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                        runOnUiThread(() -> {
                            if (isrecorder)
                                startXunfei();
                        });
                    }).start();

                }
            }

            @Override
            public void onRecordFrame(byte[] data) {
                if (mode == MODE_VOICEAI && asrsdk2 != null) {
                    asrsdk2.send(data);
                } else if (mode == MODE_Xunfei && jiyun == null) {
                    if (mIat != null) {
                        if (mIat.isListening()) {
                            mIat.writeAudio(data, 0, data.length);
                        }
                    }
                }
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        stopRecorder();
        if (item.getItemId() == R.id.baidu) {
            mode = MODE_Baidu;
            changeView();
        } else if (item.getItemId() == R.id.voiceai) {
            mode = MODE_VOICEAI;
            changeView();
        } else if (item.getItemId() == R.id.xunfei) {
            mode = MODE_Xunfei;
            changeView();
        } else if (item.getItemId() == R.id.set) {
            if (mode == MODE_VOICEAI) {
                item.getSubMenu().getItem(0).setVisible(false);
                item.getSubMenu().getItem(1).setVisible(true);
                item.getSubMenu().getItem(2).setVisible(true);
            } else if (mode == MODE_Baidu) {
                item.getSubMenu().getItem(0).setVisible(true);
                item.getSubMenu().getItem(1).setVisible(false);
                item.getSubMenu().getItem(2).setVisible(true);
            } else if (mode == MODE_Xunfei) {
                item.getSubMenu().getItem(0).setVisible(true);
                item.getSubMenu().getItem(1).setVisible(true);
                item.getSubMenu().getItem(2).setVisible(false);
            }
        } else if (item.getItemId() == R.id.angleset) {
            myShowDialog2();
        }
        return true;
    }

    public AlertDialog createDialog2(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.adduserdialog, null);
        sure2 = view.findViewById(R.id.sure);
        edit = view.findViewById(R.id.edit);
        edit.setFilters(new InputFilter[]{new MoneyInputFilter(180)});
        edit.setInputType(InputType.TYPE_CLASS_NUMBER);
        sure2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String username = edit.getText().toString().trim();
                if ("".equals(username)) {
                    Toast.makeText(DemoActivity2.this, "参数为空", Toast.LENGTH_SHORT);
                } else {
                    if (jiyun != null) {
                        jiyun.setBeamformingAngle(Integer.valueOf(username));
                    }
                    dialog2.cancel();
                }
            }
        });
        cancel2 = view.findViewById(R.id.cancel);
        cancel2.setOnClickListener(v -> dialog2.cancel());
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setView(view);
        dialog2 = builder.create();

        return dialog2;
    }

    AlertDialog dialog2;

    private void myShowDialog2() {
        if (dialog2 == null) {
            dialog2 = createDialog2(this);
        }
        dialog2.getWindow().setBackgroundDrawable(new ColorDrawable());
        dialog2.setCanceledOnTouchOutside(true);
        dialog2.show();
        WindowManager windowManager = getWindowManager();
        Display display = windowManager.getDefaultDisplay();
        WindowManager.LayoutParams lp = dialog2.getWindow().getAttributes();
        lp.width = (int) (display.getWidth() * 0.8); //设置宽度
        lp.y = 0;
        dialog2.getWindow().setAttributes(lp);
        edit.postDelayed(() -> {
            InputMethodManager inputManager = (InputMethodManager) edit.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.showSoftInput(edit, 0);
            WindowManager.LayoutParams lpl = dialog2.getWindow().getAttributes();
            lpl.y = -200;
            dialog2.getWindow().setAttributes(lpl);

        }, 100);


    }

    private void changeView() {
        destroy();
        mShowVoice.setText("");
        mShow2.setText("");
        mAnStart.setEnabled(true);
        mAnStart.setTextColor(Color.BLACK);
        if (mode == MODE_VOICEAI) {
            mTitle1.setText("麦克风阵列_VoiceAI2.0");
            mTitle2.setText("手机麦_VoiceAIV2.0");
        } else if (mode == MODE_Baidu) {
            mTitle1.setText("麦克风阵列_Baidu3.0.8.2");
            mTitle2.setText("手机麦_BaiduAPI");
        } else {
            mTitle1.setText("麦克风阵列_Xunfei_android");
            mTitle2.setText("手机麦_XunfeiAPI");
        }
        initsdk();

    }

    private void initView() {
        mToorbar = findViewById(R.id.toorbar);
        mTitle1 = findViewById(R.id.title1);
        mShowVoice = findViewById(R.id.show_voice);
        mStatueVoiceai = findViewById(R.id.statue_voiceai);
        mAngle = findViewById(R.id.angle);
        mAnglevalue = findViewById(R.id.anglevalue);
        mClearVoiceai = findViewById(R.id.clear_voiceai);
        mClearVoiceai.setOnClickListener((v) -> mShowVoice.setText(""));
        mAngleset = findViewById(R.id.angleset);
        mTitle2 = findViewById(R.id.title2);
        mShow2 = findViewById(R.id.show_baidu);
        mStatue2 = findViewById(R.id.statue_baidu);
        mClear2 = findViewById(R.id.clear_baidu);
        mClear2.setOnClickListener((v) -> mShow2.setText(""));
        mStart = findViewById(R.id.start);
        mAnStart = findViewById(R.id.an_start);
        mAnEnd = findViewById(R.id.an_end);
        mAnStart.setOnClickListener(v -> {
            mAnStart.setEnabled(false);
            mAnStart.setTextColor(Color.GRAY);
            startRecorder();
        });
        mToorbar.inflateMenu(R.menu.more);
        mToorbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                onOptionsItemSelected(item);
                return false;
            }
        });
        mAnEnd.setOnClickListener(v -> {
            mAnStart.setEnabled(true);
            mAnStart.setTextColor(Color.BLACK);
            stopRecorder();
        });
    }

}
