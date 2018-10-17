package com.voiceai.asr;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.util.Log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.concurrent.LinkedBlockingDeque;
import javax.security.auth.login.LoginException;
import static android.util.Log.*;
import com.jiyun.api.*;

@Deprecated
public class AudioRecordThread extends Thread  {
  private static final String TAG = AudioRecordThread.class.getSimpleName();
  private static final int SAMPLE_RATE_HZ = 16000;
  private int bufferSize;
  private String filePath;
  private AudioRecord audioRecord;
  private volatile boolean isStartRecord = false;
  private volatile boolean isCellphone = false;
  private LinkedBlockingDeque<byte[]> linkedBlockingDeque;
  //
  JiyunMicArrayInterface processor = new JiyunMicArrayImplement();

  public AudioRecordThread(LinkedBlockingDeque<byte[]> linkedBlockingDeque) {
    this.linkedBlockingDeque = linkedBlockingDeque;
    if (isCellphone) {
      bufferSize = AudioRecord.getMinBufferSize(SAMPLE_RATE_HZ, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
      audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC, SAMPLE_RATE_HZ, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
    } else {
      boolean ret = processor.initialize("/sdcard/mask-all.model", "", "/sdcard/Pepper/");
      // Log.d(TAG, "JiyunSpicax initialized finished");
      if (!ret) {Log.e(TAG, "cannot find device");}
    }
  }


  private class LoadingThread extends Thread {
    public void run() {
      //processor.StartRecording();
    }
  }


  public void startRecord() {
    if (isStartRecord) {
      return;
    }
    isStartRecord = true;
    this.start();
  }


  public void stopRecord() {
    isStartRecord = false;
  }

  @Override
  public void run() {
    super.run();
    Log.i(TAG, "audioRecord startRecording \n");
    isStartRecord = true;

    if (isCellphone) {
      //cellphone microphone
      audioRecord.startRecording();
      while (isStartRecord) {
        byte[] buffer = new byte[bufferSize];
        audioRecord.read(buffer, 0, bufferSize);
        linkedBlockingDeque.add(buffer);
      }
      // 释放资源
      audioRecord.stop();
      audioRecord.release();
      audioRecord = null;
      Log.i(TAG, "audioRecord release \n");
    } else {
      //microphone array
      processor.stopCapturing();
      processor.startCapturing();
      
      while (true) {
        int required_size = 1024;
        byte[] buffer = new byte [required_size];
        int read_size = processor.read(buffer, 0, required_size);
        Log.e(TAG, "get data=" + read_size + "\n");

        if (read_size > 0) {
          linkedBlockingDeque.add(buffer);
        }
        if (!isStartRecord) {break;}
      }
    }
    //
    processor.release();
    // 清空数据
    linkedBlockingDeque.clear();
  }
}