//package com.voiceai.vprcjavasdk;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.util.Log;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.Timer;
//import java.util.TimerTask;
//
//public class RefreshDemo {
//    String baseurl =
//            "https://www.voiceaitech.com:8072";
//    String AppId = "bd96e6c7bde84f07a814bfcead042101";
//    String AppSecret = "3a378f944612522e7a135227063fd836";
//
//    void test(Context context) {
//        Config config = new Config(context, AppId, AppSecret);
//        VPRCJavaSDK.getInstance().VPRCSdkLogin(baseurl, -1, config.getAppId(), config.getAppSecret(), (res, e) -> {
//            if (e == null) {
//                try {
//                    String access_token = new JSONObject(res).getString("access_token");
//                    dealAccessToken(VPRCJavaSDK.getInstance(), config, context, res);
//
//                } catch (Exception e1) {
//                    Log.e("error禁止出错", e1.toString());
//                    e1.printStackTrace();
//                }
//            } else {
//                Log.e("sdkutils", "error" + "init" + res + e.toString());
//            }
//        });
//    }
//
//    public static String getAccessToken(Context context) {
//        @SuppressLint("WrongConstant") String ac = context.getApplicationContext().getSharedPreferences("init", Context.MODE_APPEND).getString("access_token", "");
//        @SuppressLint("WrongConstant") long time = context.getApplicationContext().getSharedPreferences("init", Context.MODE_APPEND).getLong("expires_time", 0);
//        if (time > System.currentTimeMillis()) return ac;
//        return "";
//    }
//
//    public static void dealAccessToken(VPRCSDKImpl vprcNet, Config config, final Context context, String str) {
//        if (context == null) {
//            return;
//        }
//        @SuppressLint("WrongConstant") SharedPreferences.Editor edit = context.getApplicationContext().getSharedPreferences("init", Context.MODE_APPEND).edit();
//        try {
//            JSONObject re = new JSONObject(str);
//            if (re.getString("access_token").length() > 2) {
//                String accesstoken = re.getString("access_token");
//                edit.putString("access_token", accesstoken);
//                edit.commit();
//                dealTime(vprcNet, config, context, getExpricesTime(str));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//    }
//
//    private static void dealTime(VPRCSDKImpl vprcNet, Config config, final Context context, long ti) {
//        if (context == null) {
//            return;
//        }
//        @SuppressLint("WrongConstant") SharedPreferences.Editor edit = context.getApplicationContext().getSharedPreferences("init", Context.MODE_APPEND).edit();
//        edit.putLong("expires_time", System.currentTimeMillis() + ti);
//        edit.commit();
//        new Timer().schedule(new TimerTask() {
//            @Override
//            public void run() {
//                vprcNet.VPRCRefreshToke(config.getAppId(), getAccessToken(context), ((res, e) -> dealTime(vprcNet, config, context, getExpricesTime(res))));
//            }
//        }, ti);
//    }
//
//    private static long getExpricesTime(String res) {
//        JSONObject re = null;
//        try {
//            re = new JSONObject(res);
//            long ti = Long.valueOf(re.getString("expires"));
//            long p = ti * 1000;
//            return p;
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return 0;
//    }
//}
