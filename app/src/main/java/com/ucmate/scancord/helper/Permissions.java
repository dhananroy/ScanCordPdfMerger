package com.ucmate.scancord.helper;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

public class Permissions {
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    public static boolean AllPermission(Activity activity){
        final Handler handler = new Handler(Looper.getMainLooper());
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (!Gallery(activity) || !Camera(activity)){
                    Gallery(activity);
                    Camera(activity);
                }
            }
        }, 1000);

        return true;
    }

    private static boolean Gallery(Activity activity){
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
        }
        else {
            return true;
        }
        return false;
    }

    private static boolean Camera(Activity activity){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (activity.checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                activity.requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
            }
            else {
               return true;
            }
            return false;
        }
        else {
            try {
                ActivityCompat.requestPermissions(activity, new String[]{android.Manifest.permission.CAMERA}, 112);
                return true;
            }
            catch (Exception ignore){
                return false;
            }

        }
    }

}
