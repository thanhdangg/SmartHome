package com.example.smarthome.view;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.example.smarthome.R;
import com.example.smarthome.databinding.FragmentScheduleBinding;
import com.example.smarthome.model.Schedule;
import com.example.smarthome.viewmodel.ScheduleAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ScheduleFragment extends Fragment {

    private FragmentScheduleBinding binding;
    List<Schedule> scheduleList = new ArrayList<>();


    public ScheduleFragment() {
        // Required empty public constructor
    }


    public static ScheduleFragment newInstance() {
        ScheduleFragment fragment = new ScheduleFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentScheduleBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        List<String> deviceNames = Arrays.asList("Đèn", "Quạt");
        List<String> deviceRooms = Arrays.asList("Phòng khách", "Phòng ngủ");

        ArrayAdapter<String> adapterDeviceName = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, deviceNames);
        ArrayAdapter<String> adapterDeviceRoom = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, deviceRooms);

        adapterDeviceName.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterDeviceRoom.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.spinnerDeviceName.setAdapter(adapterDeviceName);
        binding.spinnerDeviceRoom.setAdapter(adapterDeviceRoom);

        binding.timePicker.setIs24HourView(true);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        ScheduleAdapter adapter = new ScheduleAdapter(getSchedules());
        binding.recyclerView.setAdapter(adapter);

        binding.btnSetSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceName = binding.spinnerDeviceName.getSelectedItem().toString();
                int hour = binding.timePicker.getHour();
                int minute = binding.timePicker.getMinute();

                @SuppressLint("DefaultLocale") String timeOn = String.format("%02d:%02d", hour, minute);
                @SuppressLint("DefaultLocale") String timeOff = String.format("%02d:%02d", hour + 1, minute);
                String deviceRoom = binding.spinnerDeviceRoom.getSelectedItem().toString();

                Schedule schedule = new Schedule(deviceName, timeOn, timeOff, deviceRoom);
                scheduleList.add(schedule);
                adapter.notifyDataSetChanged();
            }
        });


    }
    private List<Schedule> getSchedules() {
        return scheduleList;
    }


}