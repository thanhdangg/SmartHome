package com.example.smarthome.viewmodel;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DeviceStatusFetcher {
    private DeviceAdapter deviceAdapter;
    private static final String URL = "http://superbong.id.vn:1911/devices";
    public DeviceStatusFetcher(DeviceAdapter deviceAdapter) {
        this.deviceAdapter = deviceAdapter;
    }

    public void fetchDeviceStatus() {
        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(URL)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();

                    Gson gson = new Gson();
                    Type type = new TypeToken<Map<String, Map<String, Object>>>(){}.getType();
                    Map<String, Map<String, Object>> devices = gson.fromJson(responseBody, type);

                    for (Map.Entry<String, Map<String, Object>> device : devices.entrySet()) {
                        String deviceRoom = device.getKey();
                        String deviceName = deviceRoom.substring(0,1);
                        String room = deviceRoom.substring(1, 3);
                        Map<String, Object> deviceProperties = device.getValue();
                        int status = ((Double) deviceProperties.get("status")).intValue();

                        // Update the switch status based on the device status
                        updateSwitchStatus(deviceName, room, status);
                    }
                }
            }
        });
    }

    private void updateSwitchStatus(String deviceName,String room, int status) {
        // Implement this method to update the switch status based on the device status
        Log.d("DeviceStatusFetcher", "Device: " + deviceName + " Room: " + room + " Status: " + status);
        String message = status == 1 ? "B" : "T";
        message = message.concat(deviceName);
        message = message.concat(room);
        Log.d("DeviceStatusFetcher", "Message: " + message);
        deviceAdapter.updateDeviceStatus(message);

    }
    private void updateScheduleStatus(String deviceName, int status) {
        // Implement this method to update the schedule status based on the device status
    }
}