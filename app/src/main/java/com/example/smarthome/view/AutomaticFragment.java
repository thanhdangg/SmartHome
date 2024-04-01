package com.example.smarthome.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.smarthome.databinding.FragmentAutomaticBinding;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smarthome.R;

import java.io.IOException;

public class AutomaticFragment extends Fragment {

    private FragmentAutomaticBinding binding;
    public AutomaticFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentAutomaticBinding.inflate(getLayoutInflater());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAutomaticBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        boolean isDenPhongKhachOn = binding.switchDenPhongKhach.isChecked();
        boolean isQuatPhongKhachOn = binding.switchQuatPhongKhach.isChecked();
        boolean isDenPhongNguOn = binding.switchDenPhongNgu.isChecked();
        boolean isQuatPhongNguOn = binding.switchQuatPhongNgu.isChecked();

        if (isDenPhongKhachOn) {
            Log.d("AutomaticFragment", "Den Phong Khach On ");
        } else {
            Log.d("AutomaticFragment", "Den Phong Khach OFF ");
        }
        if (isQuatPhongKhachOn) {
            Log.d("AutomaticFragment", "isQuatPhongKhachOn ");
        }
        if (isDenPhongNguOn) {
            Log.d("AutomaticFragment", "isDenPhongNguOn");
        }if (isQuatPhongNguOn) {
            Log.d("AutomaticFragment", "isQuatPhongNguOn");
        }

        binding.switchDenPhongKhach.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sendRequestToESP32("denPhongKhach", isChecked);
        });

        binding.switchQuatPhongKhach.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sendRequestToESP32("quatPhongKhach", isChecked);
        });

        binding.switchDenPhongNgu.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sendRequestToESP32("denPhongNgu", isChecked);
        });

        binding.switchQuatPhongNgu.setOnCheckedChangeListener((buttonView, isChecked) -> {
            sendRequestToESP32("quatPhongNgu", isChecked);
        });

    }
    private void sendRequestToESP32(String device, boolean isOn) {
        String action = isOn ? "on" : "off";
        String url = "http://192.168.1.100:80/" + device + "?action=" + action;

        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new okhttp3.Callback() {
            @Override
            public void onFailure(okhttp3.Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(okhttp3.Call call, okhttp3.Response response) throws IOException {
                if (response.isSuccessful()) {
                    Log.d("ESP32", "Command sent successfully");
                } else {
                    Log.d("ESP32", "Failed to send command: " + response.message());
                }
            }
        });
    }
}