package com.example.smarthome.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smarthome.databinding.FragmentListDeviceBinding;
import com.example.smarthome.model.Device;
import com.example.smarthome.viewmodel.DeviceAdapter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ListDeviceFragment extends Fragment {

    private FragmentListDeviceBinding binding;
    private WebSocketUrlProvider webSocketUrlProvider;
    String webSocketUrl = "";

    public ListDeviceFragment() {
    }

    public static ListDeviceFragment newInstance(String param1, String param2) {
        ListDeviceFragment fragment = new ListDeviceFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentListDeviceBinding.inflate(getLayoutInflater());
        webSocketUrlProvider = new WebSocketUrlProvider();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListDeviceBinding.inflate(inflater, container, false);
        binding.rvDevices.setLayoutManager(new GridLayoutManager(getContext(), 2));

        List<Device> devices = new ArrayList<>();
        devices.add(new Device(1, "light", "Đèn trần", "Xiaomi Yelght", false, "Phòng khách"));
        devices.add(new Device(2, "lamp", "Đèn ngủ", "Xiaomi Fan", false, "Phòng ngủ"));
        devices.add(new Device(3, "ceilingfan", "Quạt trần", "Xiaomi", false, "Phòng khách"));
        devices.add(new Device(4, "fan", "Quạt", "Xiaomi", false, "Phòng ngủ"));
//        devices.add(new Device(5, "kitchen", "Máy khử mùi", "Xiaomi Yelght", false, "Phòng bếp"));

        webSocketUrlProvider.getWebSocketUrl(new Callback<WebSocketUrlResponse>() {
            @Override
            public void onResponse(Call<WebSocketUrlResponse> call, Response<WebSocketUrlResponse> response) {
                if (response.isSuccessful()) {
                    webSocketUrl = response.body().getWssUrl();
                    Log.d("DeviceAdapter", "WebSocket URL: " + webSocketUrl);
                    DeviceAdapter deviceAdapter = new DeviceAdapter(devices, getContext(), webSocketUrl);
                    binding.rvDevices.setAdapter(deviceAdapter);
                }
            }

            @Override
            public void onFailure(Call<WebSocketUrlResponse> call, Throwable t) {
                Log.e("DeviceAdapter", "Failed to get WebSocket URL: " + t.getMessage());
            }
        });

        Bundle args = getArguments();
        String room = args != null ? args.getString("room") : null;
        if (room == null || room.isEmpty()){
//            webSocketUrl = getWebSocketUrl();  // Replace this with the actual method to get the WebSocket URL
//            String webSocketUrl = "wss://ba51-3-25-190-4.ngrok-free.app";

            DeviceAdapter deviceAdapter = new DeviceAdapter(devices, getContext(), webSocketUrl);
            binding.rvDevices.setAdapter(deviceAdapter);
//            binding.rvDevices.setAdapter(deviceAdapter);
        }
        else {
            List<Device> devices1 = new ArrayList<>();
            for (Device device : devices) {
                if (device.getRoom().equals(room)) {
                    devices1.add(device);
                }
            }
            DeviceAdapter deviceAdapter = new DeviceAdapter(devices1);
            binding.rvDevices.setAdapter(deviceAdapter);
        }

        return binding.getRoot();
    }
    private static String extractWssValue(String jsonResponse) {
        int startIndex = jsonResponse.indexOf("\"wss\":") + "\"wss\":".length();
        int endIndex = jsonResponse.indexOf(",", startIndex);
        if (endIndex == -1) {
            endIndex = jsonResponse.indexOf("}", startIndex);
        }
        return jsonResponse.substring(startIndex, endIndex);
    }
    private static String getWebSocketUrl(){
        String url = "http://3.25.190.4:1911/wss";
        String wssValue = "";
        try {
            URL apiUrl = new URL(url);

            HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            conn.disconnect();

            String jsonResponse = response.toString();
            wssValue = extractWssValue(jsonResponse);
            Log.d("DeviceAdapter", "WebSocket URL:  Value of 'wss' key: " + wssValue);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wssValue;
    }
}