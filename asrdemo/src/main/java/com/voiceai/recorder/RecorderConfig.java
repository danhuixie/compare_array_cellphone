package com.voiceai.recorder;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;

/**
 * Created by qing on 11/01/2018.
 */

public class RecorderConfig {

    private int sampleFreq = 16000;
    private int channel = AudioFormat.CHANNEL_IN_MONO;
    private int audioFormat = AudioFormat.ENCODING_PCM_16BIT;
    private int audioSource = MediaRecorder.AudioSource.MIC;
    boolean timeControl=true;
    long mintime_msecond = 1000;
    long longtime_msecond = 60000;
    boolean yinliangControl;
    private boolean fileSaveFlag = true; //是否保存音频文件

    public boolean isFileSaveFlag() {
        return fileSaveFlag;
    }

    public void setFileSaveFlag(boolean fileSaveFlag) {
        this.fileSaveFlag = fileSaveFlag;
    }




    public boolean isTimeControl() {
        return timeControl;
    }

    public void setTimeControl(boolean timeControl) {
        this.timeControl = timeControl;
    }

    public long getMintime_msecond() {
        return mintime_msecond;
    }

    public void setMintime_msecond(long mintime_msecond) {
        this.mintime_msecond = mintime_msecond;
    }

    public long getLongtime_msecond() {
        return longtime_msecond;
    }

    public void setLongtime_msecond(long longtime_msecond) {
        this.longtime_msecond = longtime_msecond;
    }

    public boolean isYinliangControl() {
        return yinliangControl;
    }

    public void setYinliangControl(boolean yinliangControl) {
        this.yinliangControl = yinliangControl;
    }

    public Context getContext() {
        return context;
    }

    Context context;

    public RecorderConfig(Context context) {
        this.context = context;
    }

    public int getChannel() {
        return channel;
    }

    public void setChannel(int channel) {
        this.channel = channel;
    }

    public int getSampleFreq() {
        return sampleFreq;
    }

    public void setSampleFreq(int sampleFreq) {
        this.sampleFreq = sampleFreq;
    }

    public int getAudioFormat() {
        return audioFormat;
    }

    public void setAudioFormat(int audioFormat) {
        this.audioFormat = audioFormat;
    }

    public int getAudioSource() {
        return audioSource;
    }


    public void setAudioSource(int audioSource) {
        this.audioSource = audioSource;
    }

    public String getContinuousFileSavePath() {
        return getRoot(context) + "recordtem";
    }

    private static String getRoot(Context context) {
        if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
            return Environment.getExternalStorageDirectory() + "/" + context.getPackageName() + "/";
        else return context.getFilesDir() + "/";
    }

    public int getBufferSize() {
        return AudioRecord.getMinBufferSize(getSampleFreq(),
                getChannel(),
                getAudioFormat());
    }

}
