package com.openyt.app.network;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * Cliente HTTP para o servidor OpenYT Node.js.
 *
 * ⚠️ CONFIGURAÇÃO: Troque BASE_URL pelo endereço da sua VPS!
 * Exemplo: "https://meuservidor.com.br:3001"
 */
public class ApiClient {

    private static final String TAG = "ApiClient";

    // ⬇️ TROQUE AQUI pelo endereço da sua VPS ⬇️
    private static final String BASE_URL = "https://SEU_IP_OU_DOMINIO:3001";

    private static ApiClient instance;
    private final OkHttpClient httpClient;
    private final Gson gson;

    private ApiClient() {
        httpClient = new OkHttpClient.Builder()
                .connectTimeout(15, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)   // timeout maior para streams
                .writeTimeout(15, TimeUnit.SECONDS)
                .build();
        gson = new Gson();
    }

    public static synchronized ApiClient getInstance() {
        if (instance == null) {
            instance = new ApiClient();
        }
        return instance;
    }

    /** Interface de callback para respostas da API */
    public interface ApiCallback {
        void onSuccess(JsonObject response);
        void onError(String message);
    }

    /** Faz uma requisição GET para a API */
    private void get(String endpoint, final ApiCallback callback) {
        String url = BASE_URL + endpoint;
        Log.d(TAG, "GET " + url);

        Request request = new Request.Builder()
                .url(url)
                .build();

        httpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e(TAG, "Falha na requisição: " + e.getMessage());
                callback.onError(e.getMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    callback.onError("Erro HTTP " + response.code());
                    return;
                }
                try {
                    String body = response.body().string();
                    JsonObject json = JsonParser.parseString(body).getAsJsonObject();
                    callback.onSuccess(json);
                } catch (Exception e) {
                    Log.e(TAG, "Erro ao parsear JSON: " + e.getMessage());
                    callback.onError("Erro ao processar resposta");
                }
            }
        });
    }

    // ─────────────────────────────────────────────
    // ENDPOINTS
    // ─────────────────────────────────────────────

    /** Busca vídeos em destaque (trending) */
    public void getTrending(String region, ApiCallback callback) {
        get("/trending?region=" + region, callback);
    }

    /** Pesquisa vídeos por termo */
    public void search(String query, ApiCallback callback) {
        try {
            String encoded = java.net.URLEncoder.encode(query, "UTF-8");
            get("/search?q=" + encoded, callback);
        } catch (Exception e) {
            callback.onError(e.getMessage());
        }
    }

    /** Busca informações de um vídeo pelo ID */
    public void getVideoInfo(String videoId, ApiCallback callback) {
        get("/video/" + videoId, callback);
    }

    /** Busca mensagens de updates */
    public void getUpdates(ApiCallback callback) {
        get("/updates", callback);
    }

    /**
     * Retorna a URL de streaming de um vídeo.
     * O VideoView do Android usa essa URL diretamente.
     */
    public String getStreamUrl(String videoId) {
        return BASE_URL + "/hls/" + videoId + "/master.m3u8";
    }
}
