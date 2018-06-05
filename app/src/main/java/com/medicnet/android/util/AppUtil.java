package com.medicnet.android.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import com.amirarcane.lockscreen.activity.EnterPinActivity;
import com.medicnet.android.R;

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
            intent.putExtra(EnterPinActivity.EXTRA_IS_CANCELABLE, isCancelable);
        }
        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
            activity.startActivityForResult(intent, requestCode);
        }
    }

    public static void showAlerDialog(Context context, String title, String msg, boolean cancelable,
                                      String buttonText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.AlertDialogTheme);
        builder.setCancelable(cancelable);
        builder.setTitle(title);
        builder.setMessage(msg);
        builder.setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    public static void clearPasscode(Activity activity) {
        SharedPreferences prefs = activity.getSharedPreferences(EnterPinActivity.PREFERENCES,
                Context.MODE_PRIVATE);
        prefs.edit().clear().commit();
    }
}
