package com.example.smarthome.viewmodel;

import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthome.model.Schedule;
import com.example.smarthome.R;

import java.util.List;

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
        holder.timeOn.setText(schedule.getTimeOn());
        holder.timeOff.setText(schedule.getTimeOff());
        holder.deviceRoom.setText(schedule.getDeviceRoom());
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

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            deviceName = itemView.findViewById(R.id.device_name);
            timeOn = itemView.findViewById(R.id.time_on);
            timeOff = itemView.findViewById(R.id.time_off);
            deviceRoom = itemView.findViewById(R.id.device_room);
        }
    }
}