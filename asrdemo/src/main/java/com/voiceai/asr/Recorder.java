//package com.voiceai.asr;
//
//import android.media.AudioFormat;
//import android.media.AudioRecord;
//import android.media.MediaRecorder;
//import android.support.annotation.NonNull;
//import android.util.Log;
//
//import com.voiceai.vprcjavasdk.ASRSDK;
//
//import java.io.IOException;
//import java.io.InputStream;
//
///**
// * Created by fujiayi on 2017/11/27.
// */
//
//public class Recorder extends InputStream {
//    private static AudioRecord audioRecord;
//
//    private static Recorder is;
//
//    private boolean isStarted = false;
//
//    private static final String TAG = "MyMicrophoneInputStream";
//
//    public Recorder() {
//        if (audioRecord == null) {
//            int bufferSize = AudioRecord.getMinBufferSize(16000,
//                    AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT) * 16;
//            audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
//                    16000, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT, bufferSize);
//        }
//    }
//
//    public static Recorder getInstance() {
//        System.out.println("没有被调用");
//        if (is == null) {
//            synchronized (Recorder.class) {
//                if (is == null) {
//                    is = new Recorder();
//                }
//            }
//        }
//        return is;
//    }
//
//    public void start() {
//        Log.i(TAG, " MyMicrophoneInputStream start recoding!");
//        try {
//            if (audioRecord == null
//                    || audioRecord.getState() != AudioRecord.STATE_INITIALIZED) {
//                throw new IllegalStateException(
//                        "startRecording() called on an uninitialized AudioRecord." + (audioRecord == null));
//            }
//            audioRecord.startRecording();
//        } catch (Exception e) {
//            Log.e(TAG, e.getClass().getSimpleName(), e);
//        }
//        Log.i(TAG, " MyMicrophoneInputStream start recoding finished");
//    }
//
//    @Override
//    public int read() throws IOException {
//        throw new UnsupportedOperationException();
//    }
//
//    @Override
//    public int read(@NonNull byte[] b, int off, int len) throws IOException {
//        if (!isStarted) {
//            start(); // 建议在CALLBACK_EVENT_ASR_READY事件中调用。
//            isStarted = true;
//        }
//        try {
//            int count = audioRecord.read(b, off, len);
//            // Log.i(TAG, " MyMicrophoneInputStream read count:" + count);
//            if (count>0) {
//                byte[] data=new byte[count];
//                System.arraycopy(b,off,data,0,len);
////                ASRSDK.getInstance().send(data);
//            }
//            return count;
//        } catch (Exception e) {
//            Log.e(TAG, e.getClass().getSimpleName(), e);
//            throw e;
//        }
//
//    }
//
//    @Override
//    public void close() throws IOException {
//        Log.i(TAG, " MyMicrophoneInputStream close");
//        if (audioRecord != null) {
//            audioRecord.stop();
//            // audioRecord.release(); 程序结束别忘记自行释放
//            isStarted = false;
//        }
//    }
//}
