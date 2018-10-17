//package com.voiceai.asr;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.os.Environment;
//import android.os.Handler;
//import android.os.Message;
//import android.preference.PreferenceManager;
//import android.support.annotation.Nullable;
//import android.util.Log;
//import android.view.MotionEvent;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//
//import com.baidu.aip.asrwakeup3.core.inputstream.InFileStream;
//import com.baidu.aip.asrwakeup3.core.mini.AutoCheck;
//import com.baidu.aip.asrwakeup3.core.recog.MyRecognizer;
//import com.baidu.aip.asrwakeup3.core.recog.listener.IRecogListener;
//import com.jiyun.api.JiyunMicArrayImplement;
//import com.jiyun.api.JiyunMicArrayInterface;
//import com.voiceai.asrexample.R;
//import com.voiceai.common.EasyPermissionDemoActivity;
//import com.voiceai.recorder.RecorderImpl;
//import com.voiceai.vprcjavasdk.ASRSDK;
//import com.voiceai.vprcjavasdk.Config;
//import com.voiceai.vprcjavasdk.MThrowable;
//import com.voiceai.vprcjavasdk.VPRCSDK;
//
//import java.io.File;
//import java.io.IOException;
//import java.io.OutputStream;
//import java.text.SimpleDateFormat;
//import java.util.Date;
//import java.util.Iterator;
//import java.util.Map;
//
//public class DemoActivity extends EasyPermissionDemoActivity implements View.OnClickListener {
//
//    private TextView mShowVoice;
//    private TextView mStatueVoiceai;
//    private Button mClearVoiceai;
//    private TextView mShowBaidu;
//    private TextView mStatueBaidu;
//    private Button mClearBaidu;
//    private Button mStart;
//    private MyRecognizer baidusdk;
//
//    int i = 0;
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        InFileStream.setContext(this);
//        setContentView(R.layout.demo);
//        initView();
//        if (i == 1) {
//            initsdk();
//        }
//        i++;
//
//    }
//
//    // 测试发现此函数不如预期，实际底层仍然使用preference,使用时需要注意 使用时先修改对应的preference 然后发送数据
//    public static void start(Context context, MyRecognizer myRecognizer) {
//        // DEMO集成步骤2.1 拼接识别参数： 此处params可以打印出来，直接写到你的代码里去，最终的json一致即可。
//        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("vad", "touch,  关闭静音断句功能。用戶手动停止录音").commit();
//        PreferenceManager.getDefaultSharedPreferences(context).edit().putString("infile", "#com.jiyun.api.JiyunMicArrayImplement.getInstance(), 自行从麦克风读取").commit();
//        final Map<String, Object> params = new CommonRecogParams().fetch(PreferenceManager.getDefaultSharedPreferences(context));
//        (new AutoCheck(context.getApplicationContext(), new Handler() {
//            public void handleMessage(Message msg) {
//                if (msg.what == 100) {
//                    AutoCheck autoCheck = (AutoCheck) msg.obj;
//                    synchronized (autoCheck) {
//                        String message = autoCheck.obtainErrorMessage(); // autoCheck.obtainAllMessage();
////                        txtLog.append(message + "\n");
//                        ; // 可以用下面一行替代，在logcat中查看代码
//                        Log.w("AutoCheckMessage", message);
//                    }
//                }
//            }
//        }, false)).checkAsr(params);
//
//        // 这里打印出params， 填写至您自己的app中，直接调用下面这行代码即可。
//        // DEMO集成步骤2.2 开始识别
//        printMap(params);
//        myRecognizer.start(params);
//    }
//
//    static void printMap(Map<String, Object> map) {
////        map.forEach((str, ob) -> {
////            if (str != null && ob != null)
////                Log.w("AutoCheckMessage", str + "obj:" + ob.toString());
////        });
//
//        Iterator<String> a = map.keySet().iterator();
//        while (a.hasNext()) {
//            String str = a.next();
//            if (str != null && map.get(str) != null)
//                Log.w("AutoCheckMessage", str + "obj:" + map.get(str).toString());
//        }
//    }
//
//    private void initView() {
//        mShowVoice = findViewById(R.id.show_voice);
//        mStatueVoiceai = findViewById(R.id.statue_voiceai);
//        mClearVoiceai = findViewById(R.id.clear_voiceai);
//        mShowBaidu = findViewById(R.id.show_baidu);
//        mStatueBaidu = findViewById(R.id.statue_baidu);
//        mClearBaidu = findViewById(R.id.clear_baidu);
//        mStart = findViewById(R.id.start);
////        mStart.setOnClickListener(this);
//        mClearVoiceai.setOnClickListener(this);
//        mClearBaidu.setOnClickListener(this);
//        mStart.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                switch (event.getAction()) {
//                    case MotionEvent.ACTION_DOWN:
//                        if (baidusdk != null) {
//                            if (mReadThread != null) {
//                                mReadThread.cancel();
//                                mReadThread.interrupt();
//                            }
//                            SimpleDateFormat sdf=new SimpleDateFormat("YYYYMMddHHmmss");
//                            a = sdf.format(new Date())+ ".pcm";
//                            pat = Environment.getExternalStorageDirectory() + "/AAAA/M";
//                            filename = pat + "/" + a;
//
//                            mReadThread = new MReadThread(jiyun, (data -> {
//                                RecorderImpl.write(data, pat, filename);
//                                ASRSDK.getInstance().send(data);
//                            }));
//                            jiyun.startCapturing();
//                            mReadThread.start();
////                            start(DemoActivity.this, baidusdk);//录音自动开始
//                        }
//                        mStart.setText("正在录音");
//                        break;
//                    case MotionEvent.ACTION_CANCEL:
//                        mStart.setText("开始录音");
//                        jiyun.stopCapturing();
//                        mReadThread.cancel();
//                        mReadThread.interrupt();
//                        mReadThread=null;
////                        stop();
//                        ASRSDK.getInstance().sendfinal();
//                        break;
//                    case MotionEvent.ACTION_UP:
//                        mStart.setText("开始录音");
//                        jiyun.stopCapturing();
//                        mReadThread.cancel();
//                        mReadThread.interrupt();
//                        mReadThread=null;
//                        ASRSDK.getInstance().sendfinal();
//                        zhuan();
////                        stop();
//                        break;
//                }
//
//                return false;
//            }
//        });
//    }
//
//    private void zhuan() {
//        File file = new File(filename);
//        String out = file.getName().replaceAll("\\.pcm", ".wav");
//        String outfile = file.getParent() + "/" + out;
//        if (file.exists()) {
//            RecorderImpl.copyWaveFile(filename, 16000, outfile);
//        }
//
//    }
//
//    String a = System.currentTimeMillis() + ".pcm";
//    String pat = Environment.getExternalStorageState() + "/AAAA/M";
//    String filename = pat + "/" + a;
//
//    void stop() {
//        if (baidusdk != null) {
//            baidusdk.stop();
////            ASRSDK.getInstance().sendfinal();
//        }
//    }
//
//    @Override
//    public void onClick(View v) {
//        switch (v.getId()) {
//            default:
//                break;
//            case R.id.clear_voiceai:
//                mShowVoice.setText("");
//                break;
//            case R.id.clear_baidu:
//                mShowBaidu.setText("");
//                break;
//            case R.id.start:
//
//                break;
//
//        }
//    }
//
//    @Override
//    protected void onDestroy() {
//        super.onDestroy();
//        if (baidusdk != null) {
//            baidusdk.cancel();
//            baidusdk.release();
//        }
//        if (jiyun != null) {
//            jiyun.release();
//        }
////        ASRSDK.getInstance().onDestory();
//    }
//
//    @Override
//    public void next() {
//        super.next();
//        if (i == 1) {
//            initsdk();
//        }
//        i++;
//    }
//
//    private void initsdk() {
//        initRecorder();
//        initbaidu();
//        initvoiceai();
//
//    }
//
//    JiyunMicArrayInterface jiyun = new JiyunMicArrayImplement();
//    MReadThread mReadThread;
//
//    private static class MReadThread extends Thread {
//        JiyunMicArrayInterface jiyun;
//        Listener listener;
//
//        public MReadThread(JiyunMicArrayInterface jiyunMicArrayInterface, Listener listener) {
//            this.jiyun = jiyunMicArrayInterface;
//            this.listener = listener;
//        }
//
//        public void cancel() {
//            iscancel = true;
//        }
//
//        public boolean iscancel;
//
//        @Override
//        public void run() {
//            super.run();
//            try {
//                int i = 0;
//                int j = 0;
//                Log.e("DemoActivity", "jiyun.isCapturing():" + jiyun.isCapturing());
//                while (!iscancel && j <= 3) {
//                    if (false) {
//                        j++;
//                        sleep(1000);
//                        continue;
//                    }
//                    Log.e("DemoActivity", "jiyun.isCapturing():" + jiyun.isCapturing());
//                    byte[] data = new byte[1024];
//                    int k = jiyun.read(data, 0, data.length);
//                    i += k;
//                    if (listener != null) {
//                        listener.accept(data);
//                    }
//                }
//            } catch (Exception e) {
//                Log.e("DemoActivity", e.getMessage() + "jiyun.isCapturing():" + jiyun.isCapturing());
//                e.printStackTrace();
//            }
//        }
//
//        interface Listener {
//            void accept(byte[] data);
//        }
//    }
//
//    private void initRecorder() {
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//
//                Process process = null;
//                try {
//                    process = Runtime.getRuntime().exec("su");
//                    OutputStream out = process.getOutputStream();
//                    out.write((("chmod 777 /dev/snd/pcmC1D0c\nsetenforce 0\n")).getBytes());
//                    out.flush();
//                    System.out.println("spicax赋权完成");
//                    String path = "/AAAA";
//                    File file = new File(Environment.getExternalStorageDirectory().getPath() + path);
//                    if (!file.exists()) {
//                        file.mkdirs();
//                    }
//                    boolean a = jiyun.initialize("", "", file.getPath());
//                    System.out.println("demoactivity" + a);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//
//
//    }
//
//    private void initvoiceai() {
//        VPRCSDK.getInstance().init(this, new Config(this), (res, e) -> {
//            if (e == null) {
//                if (mStatueVoiceai != null) {
//                    mStatueVoiceai.setText("VPRC初始化完成");
//                    try {
//                        ASRSDK.getInstance().start(VPRCSDK.getInstance(), ASRSDK.MODEL_ASR_POWER, 16000, (resc, err) -> {
//                            if (err == null) {
//                                mStatueVoiceai.setText("ASR初始化完成");
//                                ASRSDK.getInstance().setSendByteListener(new ASRSDK.Listener() {
//                                    @Override
//                                    public void accept(String msg, boolean isfinal, Throwable err) {
//                                        if (err == null) {
//                                            if (isfinal) {
//                                                mStatueVoiceai.setText("说话结束");
//                                            } else
//                                                mStatueVoiceai.setText("说话中");
//                                            dealTextView(mShowVoice, msg, isfinal);
//                                        } else {
//                                            if (mStatueVoiceai != null) {
//                                                mStatueVoiceai.setText("ASR异常" + err.getMessage());
//                                            }
//                                        }
//                                    }
//                                });
//                            } else {
//                                if (mStatueVoiceai != null) {
//                                    mStatueVoiceai.setText("网络异常" + (e != null ? e.getMessage() : ""));
//                                }
//                            }
//                        });
//                    } catch (Exception ex) {
//                        ex.printStackTrace();
//                    }
//                }
//            } else {
//                if (mStatueVoiceai != null) {
//                    mStatueVoiceai.setText("VPRC初始化异常" + e.getMessage());
//                }
//            }
//        });
//
//    }
//
//    private void initbaidu() {
//        // DEMO集成步骤 1.1 新建一个回调类，识别引擎会回调这个类告知重要状态和识别结果
//        IRecogListener listener = new MyMessageStatusRecogListener(new Handler(), mShowBaidu, mStatueBaidu);
//
//        // DEMO集成步骤 1.2 初始化：new一个IRecogListener示例 & new 一个 MyRecognizer 示例
//        baidusdk = new MyRecognizer(this, listener);
//    }
//
//    public static void dealTextView(TextView textView, String str2, boolean boo) {
//        if (textView == null) {
//            return;
//        }
//        if (str2.contains("CRPV")) {
//            byte[] k = new byte[str2.substring(str2.indexOf("CRPV")).getBytes().length - 43];
//            System.arraycopy(str2.substring(str2.indexOf("CRPV")).getBytes(), 43, k, 0, k.length);
//            str2 = new String(k);
//            boo = true;
//        }
//        String str1 = textView.getText().toString();
//        if (boo) {
//            str2 = str2 + "\n";
//        }
//        String reg = "\n";
//        if ((str1).endsWith(reg)) {
//            str1 = str1 + str2;
//        } else {
//            String[] strs = str1.split("\n");
//            String re = "";
//            for (int i = 0; i < strs.length - 1; i++)
//                re = re + strs[i] + "\n";
//            str1 = re + str2;
//        }
//        textView.setText(str1);
//    }
//}
