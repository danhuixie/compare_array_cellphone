package com.voiceai.vprcjavasdk;

import android.os.Looper;
import android.os.Message;
import android.util.Log;

public class MainHandle extends android.os.Handler {
    Response response;

    public MainHandle() {
        super();
    }

    public MainHandle(Looper mainLooper) {
        super(mainLooper);
    }

    public Response getResponse() {
        return response;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (msg.what == 12) {
            if (msg.obj instanceof ResData) {
                Throwable e = ((ResData) msg.obj).err;
                Log.e("mhandle", ((ResData) msg.obj).response + (e == null ? "" : e.toString()));
            } else {
                Log.e("mhandle", msg.obj == null ? "null" : msg.obj.toString());
            }
            if (response != null) {
                response.response(((ResData) msg.obj).response, ((ResData) msg.obj).err);
            }
        }
    }

    public static class ResData {
        String response;
        MThrowable err;

        public ResData(String response, MThrowable err) {
            this.response = response;
            this.err = err;
        }
    }
}


