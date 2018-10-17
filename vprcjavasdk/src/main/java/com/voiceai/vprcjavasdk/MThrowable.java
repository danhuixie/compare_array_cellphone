package com.voiceai.vprcjavasdk;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class MThrowable extends Throwable {

    Throwable throwable;
    String errmsgid;//最终指向错误信息。   0; 使用throwable.getMessage  ,-2,使用hint  ，其他使用解释器。
    String hint = "";//自定义

    @Override
    public String toString() {
        return "MThrowable{" +
                "throwable=" + throwable +
                ", errmsgid='" + errmsgid + '\'' +
                ", hint='" + hint + '\'' +
                '}';
    }

    private MThrowable(Throwable throwable, String errmsgid, String hint) {
        this.throwable = throwable;
        if (errmsgid == null || errmsgid.length() == 0) {
            this.errmsgid = "0";
        } else
            this.errmsgid = errmsgid;
        if (hint != null && hint.length() != 0) {
            this.hint = hint;
        }
    }

    public static MThrowable create(Throwable throwable, String errorid, String errormsg) {
        return new MThrowable(throwable, errorid, errormsg);
    }

    public static MThrowable create(Throwable throwable, String errmsgid) {
        return create(throwable, errmsgid, null);
    }

    public static MThrowable create(Throwable throwable) {
        return create(throwable, "0", null);
    }

    @Override
    public String getMessage() {
        String re = "";
        dealthrowable2();
        if (errmsgid.equals("0")) {
            return throwable == null ? "" : dealthrowable();
        } else if (errmsgid.equals("-2")) return hint;

        if (ErrorCodeInterpreter.e != null) {
            re = ErrorCodeInterpreter.e.getMessage(this);
        }
        Log.i("mthrowable", toString());
        if (re == null || re.length() == 0) {
            re = hint;
        }
        if (re == null || re.length() == 0) {
            re = throwable == null ? "" : dealthrowable();
        }
//        if (re == null || re.length() == 0) {
//            re = errmsgid;
//        }
        return re;
    }

    private void dealthrowable2() {
        if (throwable instanceof UnknownHostException ) {
            errmsgid = "500";
        }
        if (throwable instanceof ConnectException) {
            errmsgid = "500";
        }
        if (throwable instanceof SocketTimeoutException) {
            errmsgid = "500";
        }
    }

    private String dealthrowable() {
        Log.e("mthrowable", throwable.getMessage() + "");
        throwable.printStackTrace();
        if (throwable instanceof UnknownHostException) {
            return "network error";
//        } else if (throwable instanceof NullPointerException) {
//            return "network error";
        } else {
            return "unkown error";
        }
    }


    public static MThrowable create(JSONObject jsonstr) {
        try {
            return MThrowable.create(new NullPointerException("unknown error"), jsonstr.getString("errorid"), jsonstr.getString("errormsg"));
        } catch (JSONException e) {
            e.printStackTrace();
            return MThrowable.create(new NullPointerException("unknown error"), "-2", jsonstr.toString());
        }
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public String getErrmsgid() {
        return errmsgid;
    }

    public void setErrmsgid(String errmsgid) {
        this.errmsgid = errmsgid;
    }

    public String getHint() {
        return hint;
    }

    public void setHint(String hint) {
        this.hint = hint;
    }
}
