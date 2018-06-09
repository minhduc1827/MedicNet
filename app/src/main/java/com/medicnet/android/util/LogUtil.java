package com.medicnet.android.util;

import android.util.Log;

import com.medicnet.android.BuildConfig;

public class LogUtil {

    public static void d(String tag, String msg) {
        if (BuildConfig.BUILD_TYPE.equals("debug"))
            Log.d(tag, msg);
    }

    public static void e(String tag, String msg) {
        if (BuildConfig.BUILD_TYPE.equals("debug"))
            Log.e(tag, msg);
    }
}
