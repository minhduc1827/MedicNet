package com.medicnet.android.util;

import com.medicnet.android.dagger.module.AppModule;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class RequestUtil {
    //    public static final int LOCKSCREEN_REQUEST_CODE = 123;
    public static String token = "";
    public static String userId = "";
    private static final String HEADER_TOKEN = "X-Auth-Token";
    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String TAG = "RequestUtil";

    public static void request(String url, Callback callback) {
        LogUtil.d(TAG, "request @token= " + token + " @userid= " + userId + " @url= " + url);
        OkHttpClient client = AppModule.Companion.createUnsafeOkHttpClient().build();
        Request request = new Request.Builder()
                .addHeader(HEADER_TOKEN, token)
                .addHeader(HEADER_USER_ID, userId)
                .url(url)
                .build();
        client.newCall(request).enqueue(callback);
    }
}
