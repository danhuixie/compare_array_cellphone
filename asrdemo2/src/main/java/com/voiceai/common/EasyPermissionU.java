package com.voiceai.common;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class EasyPermissionU {

    private static final String TAG = "EasyPermissionU";

    public static EasyPermissions.PermissionCallbacks getDefalutcallback(final Activity activity, final Callback callback, final String[] perm) {
        return new EasyPermissions.PermissionCallbacks() {

            @Override
            public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

            }

            @Override
            public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
                print(perms);
                Log.i(TAG, "用户授权成功");
                if (perms.size() == perm.length) {
                    callback.next();
                }
            }

            @Override
            public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {
                print(perms);
                new AppSettingsDialog.Builder(activity).build().show();
            }
        };
    }

    public static void requestPermissiDirect(Activity context, String tip, int requestCode, String[] perms, Callback callback) {
        if (EasyPermissions.hasPermissions(context, perms)) {
            callback.next();
        } else {
            EasyPermissions.requestPermissions(context, tip, requestCode, perms);
        }

    }

    public static interface Callback {
        void next();
    }


    static void print(List<String> list) {
        for (String s : list) {
            System.out.println(s);
        }
    }

}
