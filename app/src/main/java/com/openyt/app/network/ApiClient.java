package com.openyt.app.network;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class ApiClient {

    private static final String BASE_URL = "https://x1co.com.br:3001";

    public interface ApiCallback {
        void onSuccess(JsonObject response);
        void onError(String message);
    }

    private static ApiClient instance;
    private final OkHttpClient client;

    private ApiClient() {
        client = new OkHttpClient.Builder()
            .connectTimeout(15, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .build();
    }

    public static ApiClient getInstance() {
        if (instance == null) instance = new ApiClient();
        return instance;
    }

    public String getStreamUrl(String videoId) {
        return BASE_URL + "/hls/" + videoId + "/master.m3u8";
    }

    private void get(String url, ApiCallback callback) {
        Request request = new Request.Builder().url(url).build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                callback.onError(e.getMessage());
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    String body = response.body().string();
                    JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                    callback.onSuccess(json);
                } catch (Exception e) {
                    callback.onError(e.getMessage());
                }
            }
        });
    }

    public void getTrending(ApiCallback callback) {
        get(BASE_URL + "/trending?region=BR", callback);
    }

    public void search(String query, ApiCallback callback) {
        get(BASE_URL + "/search?q=" + query, callback);
    }

    public void getVideoInfo(String videoId, ApiCallback callback) {
        get(BASE_URL + "/video/" + videoId, callback);
    }

    public void getUpdates(ApiCallback callback) {
        get(BASE_URL + "/updates", callback);
    }
}
