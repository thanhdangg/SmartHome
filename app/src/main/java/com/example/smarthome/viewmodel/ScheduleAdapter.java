package com.example.smarthome.viewmodel;

import android.app.AlertDialog;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthome.model.Schedule;
import com.example.smarthome.R;

import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttp;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class ScheduleAdapter extends RecyclerView.Adapter<ScheduleAdapter.ViewHolder> {
    private List<Schedule> scheduleList;

    public ScheduleAdapter(List<Schedule> scheduleList) {
        this.scheduleList = scheduleList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Schedule schedule = scheduleList.get(position);
        holder.deviceName.setText(schedule.getDeviceName());
        holder.deviceRoom.setText(schedule.getDeviceRoom());
        if (schedule.getTimeOn() != null && !schedule.getTimeOn().isEmpty()) {
            holder.status.setText("Bật");
            holder.timeOn.setText(schedule.getTimeOn());
            holder.timeOn.setVisibility(View.VISIBLE);

        }
        else {
            holder.timeOn.setVisibility(View.GONE);
        }
        if (schedule.getTimeOff() != null && !schedule.getTimeOff().isEmpty()) {
            holder.status.setText("Tắt");
            holder.timeOff.setText(schedule.getTimeOff());
            holder.timeOff.setVisibility(View.VISIBLE);
        }
        else {
            holder.timeOff.setVisibility(View.GONE);
        }
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                int currentPosition = holder.getAdapterPosition();
                if (currentPosition != RecyclerView.NO_POSITION) {
                    new AlertDialog.Builder(v.getContext())
                            .setTitle("Xóa lịch trình")
                            .setMessage("Bạn có chắc chắn muốn xóa lịch này không?")
                            .setPositiveButton("Có", (dialog, which) -> {
                                scheduleList.remove(currentPosition);
                                notifyItemRemoved(currentPosition);

                                Schedule schedule = scheduleList.get(currentPosition);
                                String deviceName = schedule.getDeviceName().equals("Đèn") ? "D" : "Q";
                                String deviceRoom = schedule.getDeviceRoom().equals("Phòng khách") ? "PK" : "PN";
                                String timeOn = schedule.getTimeOn();
                                String timeOff = schedule.getTimeOff();
                                int deviceStatus = schedule.getDeviceStatus();
                                Log.d("ScheduleAdapter", "onLongClick: " + deviceName + " " + deviceRoom + " timeOn: " + timeOn + " timeOff" + timeOff + " " + deviceStatus);

                                JSONObject json = new JSONObject();
                                try {
                                    json.put("device", deviceName + deviceRoom);
                                    if (timeOn != "") {
                                        json.put("time", timeOn);
                                        json.put("status", 1);
                                    } else {
                                        json.put("time", timeOff);
                                        json.put("status", 0);
                                    }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                                RequestBody body = RequestBody.create(json.toString(), MediaType.parse("application/json; charset=utf-8"));
                                OkHttpClient client = new OkHttpClient();
                                Request request = new Request.Builder()
                                        .url("http://superbong.id.vn:1911/timer/delete")
                                        .post(body)
                                        .build();

                                client.newCall(request).enqueue(new Callback() {
                                    @Override
                                    public void onFailure(Call call, IOException e) {
                                        Log.e("ScheduleAdapter", "onFailure: " + e.getMessage());
                                    }

                                    @Override
                                    public void onResponse(Call call, Response response) throws IOException {
                                        Log.d("ScheduleAdapter", "onResponse: " + response.body().string());
                                    }
                                });
                            })
                            .setNegativeButton("Không", null)
                            .show();
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return scheduleList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView deviceName;
        TextView timeOn;
        TextView timeOff;
        TextView deviceRoom;
        TextView status;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.device_name);
            timeOn = itemView.findViewById(R.id.time_on);
            timeOff = itemView.findViewById(R.id.time_off);
            deviceRoom = itemView.findViewById(R.id.device_room);
            status = itemView.findViewById(R.id.status);
        }
    }
}