package com.example.smarthome.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.smarthome.R;
import com.example.smarthome.databinding.FragmentListDeviceBinding;
import com.example.smarthome.model.Device;
import com.example.smarthome.viewmodel.DeviceAdapter;

import java.util.ArrayList;
import java.util.List;

public class ListDeviceFragment extends Fragment {

    private FragmentListDeviceBinding binding;

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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentListDeviceBinding.inflate(inflater, container, false);
        binding.rvDevices.setLayoutManager(new GridLayoutManager(getContext(), 2));
        List<Device> devices = new ArrayList<>();
        devices.add(new Device("light", "Đèn trần", "Xiaomi Yelght", false, "Phòng khách"));
        devices.add(new Device("fan", "Quạt trần", "Xiaomi Fan", false, "Phòng ngủ"));
        devices.add(new Device("tivi", "Smart TV", "Xiaomi", false, "Phòng khách"));
        devices.add(new Device("door", "Cửa chính", "", false, "Phòng khách"));

        Bundle args = getArguments();
        String room = args != null ? args.getString("room") : null;
        if (room == null || room.isEmpty()){
            DeviceAdapter deviceAdapter = new DeviceAdapter(devices);
            binding.rvDevices.setAdapter(deviceAdapter);
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

//        DeviceAdapter deviceAdapter = new DeviceAdapter(devices);
//        binding.rvDevices.setAdapter(deviceAdapter);
        return binding.getRoot();
    }
}