package com.example.smarthome.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smarthome.databinding.FragmentListDeviceBinding;
import com.example.smarthome.model.Device;
import com.example.smarthome.viewmodel.DeviceAdapter;
import com.example.smarthome.viewmodel.DeviceStatusUpdater;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ListDeviceFragment extends Fragment implements DeviceStatusUpdater {

    private FragmentListDeviceBinding binding;
    private WebSocketUrlProvider webSocketUrlProvider;
    String webSocketUrl = "";
    private DeviceAdapter deviceAdapter;


    public ListDeviceFragment() {
    }

    public static ListDeviceFragment newInstance() {
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

                    Bundle args = getArguments();
                    String room = args != null ? args.getString("room") : null;
                    if (room == null || room.isEmpty()){
                        deviceAdapter = new DeviceAdapter(devices, getContext(), webSocketUrl);
                    }
                    else {
                        List<Device> devices1 = new ArrayList<>();
                        for (Device device : devices) {
                            if (device.getRoom().equals(room)) {
                                devices1.add(device);
                            }
                        }
                        deviceAdapter = new DeviceAdapter(devices1, getContext(), webSocketUrl);
                    }
                    binding.rvDevices.setAdapter(deviceAdapter);
                }
            }

            @Override
            public void onFailure(Call<WebSocketUrlResponse> call, Throwable t) {
                Log.e("WebSocketClient", "Failed to get WebSocket URL: " + t.getMessage());
            }
        });

        return binding.getRoot();
    }
    @Override
    public void updateDeviceStatus(String message) {
        deviceAdapter.updateDeviceStatus(message);

    }

}