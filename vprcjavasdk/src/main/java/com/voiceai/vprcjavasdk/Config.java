package com.voiceai.vprcjavasdk;

import android.annotation.SuppressLint;
import android.content.Context;

import java.util.UUID;

public class Config {
    public static boolean productmode = false;
    public static int CONNECT_TIMEOUT = 4000;
    public static int READ_TIMEOUT = 6000;

    public static boolean ASV_CHECK = false;
    private final Context context;
    String baseurl =
            "https://cloud.voiceaitech.com:8072";

    String AppId = "bd96e6c7bde84f07a814bfcead042101";
    String AppSecret = "3a378f944612522e7a135227063fd836";
    String modeltype = VPRCSDK.MODEL_SHORT_CN_DNN;

    public Config(Context context) {
        this.context = context;
    }

    public Config(Context context, String appId, String appSecret) {
        AppId = appId;
        AppSecret = appSecret;
        this.context = context;
    }

    public Config(Context context, String baseurl, String appId, String appSecret, String modeltype) {
        this.context = context;
        this.baseurl = baseurl;
        AppId = appId;
        AppSecret = appSecret;
        this.modeltype = modeltype;
    }

    public String getBaseurl() {
        return baseurl;
    }

    public void setBaseurl(String baseurl) {
        this.baseurl = baseurl;
    }

    public String getAppId() {
        return AppId;
    }

    public void setAppId(String appId) {
        AppId = appId;
    }

    public String getAppSecret() {
        return AppSecret;
    }

    public void setAppSecret(String appSecret) {
        AppSecret = appSecret;
    }

    @SuppressLint("WrongConstant")
    public String getGroup_name() {
        String str = context.getSharedPreferences("init", Context.MODE_APPEND).getString("groupname", "");
        if ("".equals(str)) {
            str = UUID.randomUUID().toString().replaceAll("-", "");
            context.getSharedPreferences("init", Context.MODE_APPEND).edit().putString("groupname", str).commit();
        }
        return str;
    }

    public String getModeltype() {
        return modeltype;
    }

    public void setModeltype(String modeltype) {
        this.modeltype = modeltype;
    }
}
