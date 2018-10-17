package com.voiceai.asr;

import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.voiceai.asrexample2.R;
import com.voiceai.common.EasyPermissionDemoActivity;
import com.voiceai.recorder.Recorder;
import com.voiceai.recorder.RecorderImpl;
import com.voiceai.recorder.RecorderListener;
import com.voiceai.vprcjavasdk.ASRSDK;
import com.voiceai.vprcjavasdk.Config;
import com.voiceai.vprcjavasdk.MWebSocketClient;
import com.voiceai.vprcjavasdk.VPRCNetImpl;
import com.voiceai.vprcjavasdk.VPRCSDK;
import com.voiceai.vprcjavasdk.VPRCSDKImpl;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;

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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.demo);
        initView();
        initmy();
    }

    private void initmy() {
        if (i == 1)
            initsdk();
        i++;
    }

    int i;

    @Override
    public void next() {
        super.next();
        initmy();
    }

    public int mode;
    public final static int MODE_VOICEAI = 0;
    public final static int MODE_Baidu = 1;


    private void initsdk() {
        if (mode == MODE_VOICEAI) {
            initVoiceAI();
        } else {
            initBaidu();
        }
        initRecorder();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (asrsdk1 != null) {
            asrsdk1.onDestory();
        }
        if (asrsdk2 != null) {
            asrsdk2.onDestory();
        }
//        if (jiyun != null) {
//            jiyun.release();
//        }
//        if (mReadThread != null) {
//            mReadThread.cancel();
//        }
        RecorderImpl.getInstance(this).onDestory();

    }

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
//                int n = 0;
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
//                    n++;
//                    if (n % 100 == 0) {
//                        listener.accept2(jiyun.getBeamformingAngle());
//                    }
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
//
//            void accept2(int data);
//        }
//    }

    private void startRecorder() {
//        if (jiyun != null) {
//            jiyun.startCapturing();
//        }
//        if (mReadThread != null) {
//            mReadThread.cancel();
//            mReadThread.interrupt();
//        }
//        if (jiyun != null && asrsdk1 != null) {
//            mReadThread = new MReadThread(jiyun, new MReadThread.Listener() {
//                @Override
//                public void accept(byte[] data) {
//                    if (asrsdk1 != null) {
//                        asrsdk1.send(data);
//                    }
//                }
//
//                @Override
//                public void accept2(int data) {
//                    mAnglevalue.post(() -> mAnglevalue.setText("" + data));
//                }
//            });
//            mReadThread.start();
//        }
        RecorderImpl.getInstance(this).startRecorder();
    }

    private void stopRecorder() {
//        if (jiyun != null) {
//            jiyun.stopCapturing();
//        }
//        if (mReadThread != null) {
//            mReadThread.cancel();
//            mReadThread.interrupt();
//            mReadThread = null;
//        }
        RecorderImpl.getInstance(this).stopRecorder();
        if (asrsdk1 != null) {
            asrsdk1.sendfinal();
        }
        if (asrsdk2 != null) {
            asrsdk2.sendfinal();
        }

    }


    private void initBaidu() {
        //todo....
    }

    ASRSDK asrsdk1;
    ASRSDK asrsdk2;

    private void initVoiceAI() {
        if (asrsdk1 == null) {
            Config config = new Config(this);
            config.setBaseurl("https://192.168.0.100:28072");
            config.setAppId("000eacad8a56440fac5f0b8aed07ab48");
            config.setAppSecret("222eacad8a56440fac5f0b8aed07ab48");
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

//    JiyunMicArrayInterface jiyun;
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
                    String cmd = "chmod 777 /dev/snd/pcmC1D0c";
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

//                    System.out.println("spicax赋权完成+waitfor:" + process.waitFor());
//
//
//                    String path = "/AAAA";
//                    File file = new File(Environment.getExternalStorageDirectory().getPath() + path);
//                    if (!file.exists()) {
//                        file.mkdirs();
//                    }
//                    jiyun = new JiyunMicArrayImplement();
//                    boolean a = jiyun.initialize("", "", file.getPath());
//                    if (a) {
//                        runOnUiThread(() -> {
//                            Toast.makeText(DemoActivity2.this, "阵列麦克风初始化成功", Toast.LENGTH_SHORT).show();
//                        });
//                    } else {
//                        runOnUiThread(() -> {
//                            Toast.makeText(DemoActivity2.this, "阵列麦克风初始化失败", Toast.LENGTH_SHORT).show();
//                        });
//                    }
//                    System.out.println("demoactivity" + a);
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
            }

            @Override
            public void onRecordFrame(byte[] data) {
                if (mode == MODE_VOICEAI && asrsdk2 != null) {
                    asrsdk2.send(data);
                }
                if (mode == MODE_VOICEAI && asrsdk1 != null) {
                    asrsdk1.send(data);
                }
            }
        });

    }


    private void initView() {
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
            startRecorder();
        });

        mAnEnd.setOnClickListener(v -> {
            mAnStart.setEnabled(true);
            stopRecorder();
        });
    }
}
