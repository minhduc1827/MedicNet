package com.medicnet.android.util;

import com.medicnet.android.dagger.module.AppModule;

import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class RequestUtil {
    public static String token = "";
    public static String userId = "";
    private static final String HEADER_TOKEN = "X-Auth-Token";
    private static final String HEADER_USER_ID = "X-User-Id";
    private static final String HEADER_CONTENT_TYPE = "Content-Type";
    private static final String HEADER_CONTENT_TYPE_VALUE = "application/json";
    private static final String TAG = "RequestUtil";
    private static final MediaType JSON
            = MediaType.parse("application/json; charset=utf-8");

    private static final String HOST = "https://medicappdev.eastus.cloudapp.azure.com/api/v1/";
    public static final String GET_ORGANIZATION_LIST_URL = HOST + "organizations.list";
    public static final String GET_USER_LIST_URL = HOST + "users.list";

    public static void handleGetRequest(String url, Callback callback) {
        LogUtil.d(TAG, "handleGetRequest @token= " + token + " @userid= " + userId + " @url= " + url);
        OkHttpClient client = AppModule.Companion.createUnsafeOkHttpClient().build();
        Request request = new Request.Builder()
                .addHeader(HEADER_TOKEN, token)
                .addHeader(HEADER_USER_ID, userId)
                .url(url)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void handlePostRequest(String url, Callback callback, String jsonParam) {
        LogUtil.d(TAG, "handlePostRequest @token= " + token + " @userid= " + userId + " @url= " + url);
//        OkHttpClient client = AppModule.Companion.createUnsafeOkHttpClient().build();
        OkHttpClient client = new OkHttpClient();
        RequestBody body = RequestBody.create(JSON, jsonParam);
        Request request = new Request.Builder()
                .addHeader(HEADER_TOKEN, token)
                .addHeader(HEADER_USER_ID, userId)
                .addHeader(HEADER_CONTENT_TYPE, HEADER_CONTENT_TYPE_VALUE)
                .url(url)
                .post(body)
                .build();

        client.newCall(request).enqueue(callback);
    }
}
