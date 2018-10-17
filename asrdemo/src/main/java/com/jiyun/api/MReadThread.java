package com.jiyun.api;

import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.voiceai.recorder.RecorderImpl;

import java.io.File;

import static com.voiceai.recorder.RecorderImpl.zhuan;

public class MReadThread extends Thread {
    JiyunMicArrayInterface jiyun;
    Listener listener;
    Handler mhandler;

    public MReadThread(JiyunMicArrayInterface jiyunMicArrayInterface, Listener listener) {
        this.jiyun = jiyunMicArrayInterface;
        this.listener = listener;
        mhandler=new Handler(Looper.getMainLooper());
    }

    public void cancel() {
        iscancel = true;
    }

    public boolean iscancel;

    @Override
    public void run() {
        super.run();
        try {
            int i = 0;
            int n = 0;
            Log.e("DemoActivity", "jiyun.isCapturing():" + jiyun.isCapturing());
            long time = 0;
            String filename = null;
            String filepath = Environment.getExternalStorageDirectory().getPath() + "/AAAA_60_item";
            while (!iscancel && jiyun.isCapturing()) {
//                    Log.e("DemoActivity", "jiyun.isCapturing():" + jiyun.isCapturing());
                byte[] data = new byte[1024];
                int k = jiyun.read(data, 0, data.length);
                n++;
                if (n % 10 == 0) {
                    if (jiyun.getSoundLocalization() != null) {
                        mhandler.post(()->{
                            if (listener != null) {
                                listener.onAnglechange(jiyun.getSoundLocalization()[0]);
                            }
                        });
                    }
                }
                i += k;
                if (listener != null) {
                    listener.onRecordFrame(data);
                }
                if (filename == null) {
                    filename = System.currentTimeMillis() + ".pcm";
                    time = System.currentTimeMillis();
                }
                RecorderImpl.write(data, filepath, filename);
                if (System.currentTimeMillis() > time + 10000) {
                   if( zhuan(filepath, filename)){
                       String finalFilename = filename;
                       mhandler.post(()->{
                           if (listener != null) {
                               listener.onRecordEnd(filepath+"/"+ finalFilename.replaceAll("\\.pcm", ".wav"));
                           }
                       });
                   }
                    filename = null;
                }
            }
            if (filename != null) {
                zhuan(filepath, filename);
            }
        } catch (Exception e) {
            Log.e("DemoActivity", e.getMessage() + "jiyun.isCapturing():" + jiyun.isCapturing());
            e.printStackTrace();
        }
    }

}