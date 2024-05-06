package com.example.smarthome.view;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("/wss")
    Call<WebSocketUrlResponse> getWebSocketUrl();
}
