package com.voiceai.common;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.util.Log;

/**
 * ---------------------
 * 作者：暴走邻家
 * 来源：CSDN
 * 原文：https://blog.csdn.net/bzlj2912009596/article/details/70254463?utm_source=copy
 * 版权声明：本文为博主原创文章，转载请附上博文链接！
 */
public class MateData {


    private static final String TAG = "MataData";

    /**
     * 读取application 节点  meta-data 信息
     */
    public static String readMetaDataFromApplication(Context context, String tag) {
        try {
            ApplicationInfo appInfo = context.getPackageManager()
                    .getApplicationInfo(context.getPackageName(),
                            PackageManager.GET_META_DATA);
            String mTag;
            mTag = appInfo.metaData.get(tag).toString();
            Log.e(TAG, tag + "=" + mTag);
            return mTag;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 读取BroadcastReceiver 节点  meta-data 信息
     */
    public static String readMetaDataFromBroadCast(Context context, String tag, Class cla) {
        try {
            ComponentName cn = new ComponentName(context, cla);
            ActivityInfo info = context.getPackageManager().getReceiverInfo(cn,
                    PackageManager.GET_META_DATA);
            String mTag = info.metaData.getString(tag);
            Log.e(TAG, tag + "=" + mTag);
            return mTag;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 读取Service 节点  meta-data 信息
     */
    public static String readMetaDataFromService(Context context, String tag, Class cla) {
        try {
            ComponentName cn = new ComponentName(context, cla);
            ServiceInfo info = context.getPackageManager().getServiceInfo(cn,
                    PackageManager.GET_META_DATA);
            String mTag = info.metaData.getString(tag);
            Log.e(TAG, tag + "=" + mTag);
            return mTag;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 读取Activity 节点  meta-data 信息
     */
    public static String readMetaDataFromActivity(Activity context, String tag) {
        ActivityInfo info;
        try {
            info = context.getPackageManager().getActivityInfo(context.getComponentName(),
                    PackageManager.GET_META_DATA);
            String mTag = info.metaData.getString(tag);
            Log.e(TAG, tag + "=" + mTag);

//            //读取图片资源id
//            int mResource = info.metaData.getInt("mResource");
//
//            iv_pic.setImageResource(mResource);

            return mTag;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }

    }


}
