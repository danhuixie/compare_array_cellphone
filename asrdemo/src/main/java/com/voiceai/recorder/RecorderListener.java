package com.voiceai.recorder;

/**
 * Created by qing on 10/01/2018.注意非异步接口
 */

public interface RecorderListener {
    void onRecordStart();

    void onAudioError(int errCode, String msg);//使用errorcode方式解析，msg无用。

    void onVolumeChange(double volume);

    void onRecordEnd(String filePath);//文件方式  注意文件过长时，将多次分段返回

    void onRecordFrame(byte[] data);//注意此处由于性能的缘故没有切换线程，请不要UI操作，
}
