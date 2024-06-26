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

import org.jetbrains.annotations.NotNull;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

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

        OkHttpClient client = new OkHttpClient();

        // Create a request
        Request request = new Request.Builder()
                .url("http://superbong.id.vn:1911/devices")
                .build();

        // Send the request
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseBody = response.body().string();
                    try {
                        JSONObject jsonObject = new JSONObject(responseBody);
                        Iterator<String> keys = jsonObject.keys();
                        while (keys.hasNext()) {
                            String key = keys.next();
                            JSONObject deviceObject = jsonObject.getJSONObject(key);
                            JSONObject timerObject = deviceObject.getJSONObject("timer");
                            Iterator<String> timerKeys = timerObject.keys();
                            while (timerKeys.hasNext()) {
                                String time = timerKeys.next();
                                int status = timerObject.getInt(time);
                                String deviceName = key.substring(0, 1).equals("D") ? "Đèn" : "Quạt";
                                String deviceRoom = key.substring(1).equals("PK") ? "Phòng khách" : "Phòng ngủ";
                                String timeOn = status == 1 ? time : "";
                                String timeOff = status == 0 ? time : "";
                                Schedule schedule = new Schedule(deviceName, timeOn, timeOff, deviceRoom, status);
                                scheduleList.add(schedule);
                            }
                        }
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ScheduleAdapter adapter = new ScheduleAdapter(scheduleList);
                                binding.recyclerView.setAdapter(adapter);
                            }
                        });
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        });

        binding.btnSetSchedule.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String deviceName = binding.spinnerDeviceName.getSelectedItem().toString();
                String deviceRoom = binding.spinnerDeviceRoom.getSelectedItem().toString();

                int hour = binding.timePicker.getHour();
                int minute = binding.timePicker.getMinute();
                String time = String.format("%02d:%02d", hour, minute);

                String timeOn = "";
                String timeOff = "";

                int selectedId = binding.radioGroup.getCheckedRadioButtonId();
                int status = 0;
                if (selectedId == R.id.radio_on) {
                    timeOn = time;
                    status = 1;
                } else if (selectedId == R.id.radio_off) {
                    timeOff = time;
                    status = 0;
                }
                Schedule schedule = new Schedule(deviceName, timeOn, timeOff, deviceRoom, status);
                scheduleList.add(0, schedule);
                ScheduleAdapter adapter = (ScheduleAdapter) binding.recyclerView.getAdapter();
                if (adapter != null) {
//                    adapter.notifyDataSetChanged();
                    adapter.notifyItemInserted(0);
                    binding.recyclerView.scrollToPosition(0);
                }

                OkHttpClient client = new OkHttpClient();

                JSONObject json = new JSONObject();
                try {
                    String deviceType = deviceName.equals("Đèn") ? "D" : "Q";
                    String deviceLocation = deviceRoom.equals("Phòng khách") ? "PK" : "PN";
                    String device = deviceType + deviceLocation;
//                    int status = selectedId == R.id.radio_on ? 1 : 0;

                    json.put("device", device);
                    json.put("time", time);
                    json.put("status", status);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                // Create a RequestBody (Optional, only if not sending in JSON format)
                RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json; charset=utf-8"));

                // Create a request
                Request request = new Request.Builder()
                        .url("http://superbong.id.vn:1911/timer/create")
                        .post(body)
                        .build();

                // Send the request
                client.newCall(request).enqueue(new Callback() {
                    @Override
                    public void onFailure(@NotNull Call call, @NotNull IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                        if (response.isSuccessful()) {
                            // Handle the response
                        }
                    }
                });

            }
        });


    }
    private List<Schedule> getSchedules() {
        return scheduleList;
    }


}