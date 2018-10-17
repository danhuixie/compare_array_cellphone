package com.jiyun.api;

import com.voiceai.recorder.RecorderListener;

public interface Listener extends RecorderListener{


        void onAnglechange(int data);
        
    }