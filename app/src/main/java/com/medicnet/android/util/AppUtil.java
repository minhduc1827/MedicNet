package com.medicnet.android.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.amirarcane.lockscreen.activity.EnterPinActivity;

public class AppUtil {

//    public static final int LOCKSCREEN_REQUEST_CODE = 123;

    public static void displayLockScreen(Activity activity, boolean isCancelable, int requestCode) {
        SharedPreferences prefs = activity.getSharedPreferences(EnterPinActivity.PREFERENCES, Context.MODE_PRIVATE);
        Intent intent = null;
        if (prefs.getString(EnterPinActivity.KEY_PIN, "").equals("")) {
            //no pin need to set pin first
            intent = EnterPinActivity.getIntent(activity, true, isCancelable);
        } else {
            // already pin
            intent = new Intent(activity, EnterPinActivity.class);
        }
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            activity.startActivityForResult(intent, requestCode);
        }
    }
}
