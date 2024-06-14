package com.example.smarthome.viewmodel;

import com.example.smarthome.view.ApiService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebSocketUrlProvider {
    private static final String BASE_URL = "http://superbong.id.vn:1911/wss/";
    private ApiService apiService;

    public WebSocketUrlProvider() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        apiService = retrofit.create(ApiService.class);
    }

    public void getWebSocketUrl(Callback<WebSocketUrlResponse> callback) {
        Call<WebSocketUrlResponse> call = apiService.getWebSocketUrl();
        call.enqueue(callback);
    }
}

