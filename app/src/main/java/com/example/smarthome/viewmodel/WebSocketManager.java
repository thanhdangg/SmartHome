package com.example.smarthome.viewmodel;

import dev.gustavoavila.websocketclient.WebSocketClient;

public class WebSocketManager {
    private static WebSocketManager instance;
    private WebSocketClient mWebSocketClient;

    private WebSocketManager() {
    }

    public static synchronized WebSocketManager getInstance() {
        if (instance == null) {
            instance = new WebSocketManager();
        }
        return instance;
    }

    public void setWebSocketClient(WebSocketClient client) {
        this.mWebSocketClient = client;
    }

    public WebSocketClient getWebSocketClient() {
        return mWebSocketClient;
    }
}