package com.iflytek.voicedemo.recorder;


import android.content.Context;

public  interface Recorder {

    public static Recorder getInstance(Context context){
        return RecorderImpl.getInstance(context);
    }

    void setRecorderListener(RecorderListener listener);

    void setRecorderConfig(RecorderConfig recorderConfig);

    void updataConfig();

    void startRecorder();

    void stopRecorder();

    RecorderConfig getRecorderConfig();

    void onDestory();
    //录音错误码
    public static int STATUE_INIT = 0;
    public static int STATUE_RUNNING = 1;
    public static int STATUE_FINISH = 2;
    public static int STATUE_STOP = -1;
    public static int STATUE_CANCEL = -2;
    public static final int ERROR_TOO_SHORT = 1000;
    public static final int ERROR_TOO_LONG = 1001;
    public static final int ERROR_VOICE_TOO_LOW = 1002;
    public static final int ERROR_UNKNOWN= -1;
}
