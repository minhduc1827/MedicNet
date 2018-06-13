package com.medicnet.android.util;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;

import com.amirarcane.lockscreen.activity.EnterPinActivity;
import com.medicnet.android.R;
import com.medicnet.android.contacts.model.UserItem;
import com.medicnet.android.contacts.model.Users;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AppUtil {


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

    public static String convertToDate(long timestamp) {
        SimpleDateFormat formatter = new SimpleDateFormat("MMM dd, yyyy");
        return formatter.format(new Date(timestamp));
    }

    public static List<UserItem> getUserList(String json, String username) {
        List<UserItem> userItemList = new ArrayList<>();
        if (!json.isEmpty()) {
            Moshi moshi = new Moshi.Builder().build();
//            Type type= Types.newParameterizedType(List.class, Organization.class);
//            JsonAdapter<List<Organization>>adapter=moshi.adapter(type);
            JsonAdapter<Users> jsonAdapter = moshi.adapter(Users.class);
            try {
                Users userList = jsonAdapter.fromJson(json);
                for (UserItem userItem : userList.users) {
                    if (!userItem.username.equals(username))
                        userItemList.add(userItem);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return userItemList;
    }

    public static Uri saveImage(Context context, Bitmap bitmap, int quality) {
        ContextWrapper cw = new ContextWrapper(context);
        // path to /data/data/yourapp/app_data/imageDir
        File directory = cw.getDir("image", Context.MODE_PRIVATE);
        // Create imageDir
        File mypath = new File(directory, "img_" + System.currentTimeMillis() + ".png");

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmap.compress(Bitmap.CompressFormat.PNG, quality, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return Uri.fromFile(new File(directory.getAbsolutePath()));
    }
}
