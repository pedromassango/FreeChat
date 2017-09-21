package com.aman.freechat.utils;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;

/**
 * Created by Aman Grover on 30-04-2017.
 */

public class GetPermissions {
    private static String[] PERMISSIONS = {Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    public static String[] STORAGE_PERMISSIONS = {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};


    public static final int ALL_PERMISSIONS = 1001;

    public static boolean hasPermissions(Context context, String... permissions) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void askAllPermissions(Activity context) {
        ActivityCompat.requestPermissions(context, PERMISSIONS, ALL_PERMISSIONS);
    }

    public static boolean checkStoragePermission(Context context) {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && STORAGE_PERMISSIONS != null) {
            for (String permission : STORAGE_PERMISSIONS) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean checkCameraPermission(Context context) {
        String permission = Manifest.permission.CAMERA;
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && context != null && STORAGE_PERMISSIONS != null) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        return true;
    }
}
