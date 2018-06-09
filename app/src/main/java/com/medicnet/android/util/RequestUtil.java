package com.medicnet.android.util;

import com.medicnet.android.dagger.module.AppModule;

import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;

public class RequestUtil {
    public static String token = "";
    public static String userId = "";
    private static final String HEADER_TOKEN = "X-Auth-Token";
    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String TAG = "RequestUtil";
    private static final String HOST = "https://medicappdev.eastus.cloudapp.azure.com/api/v1/";
    public static final String GET_ORGANIZATION_LIST_URL = HOST + "organizations.list";
    public static final String GET_USER_LIST_URL = HOST + "users.list";

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
