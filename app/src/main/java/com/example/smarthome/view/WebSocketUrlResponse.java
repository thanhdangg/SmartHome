package com.example.smarthome.view;

import com.google.gson.annotations.SerializedName;

public class WebSocketUrlResponse {
    @SerializedName("wss")
    private String wssUrl;

    public String getWssUrl() {
        return wssUrl;
    }
}

