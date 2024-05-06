package com.example.smarthome.view;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WebSocketUrlProvider {
    private static final String BASE_URL = "http://3.25.190.4:1911/";
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

