package com.example.smarthome.viewmodel;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthome.databinding.DeviceItemBinding;
import com.example.smarthome.model.Device;

import java.util.List;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private List<Device> devices;

    public DeviceAdapter(List<Device> devices) {
        this.devices = devices;
    }

    public class DeviceViewHolder extends RecyclerView.ViewHolder {

        public DeviceItemBinding binding;
        public DeviceViewHolder(DeviceItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

    @NonNull
    @Override
    public DeviceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeviceViewHolder(DeviceItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DeviceViewHolder holder, int position) {
        Device device = devices.get(position);
        holder.binding.ivDeviceIcon.setImageResource(holder.itemView.getContext().getResources().getIdentifier(device.getIconPath(), "drawable", holder.itemView.getContext().getPackageName()));
        holder.binding.tvDeviceName.setText(device.getName());
//        holder.binding.tvDeviceType.setText(device.getType());
        holder.binding.swDeviceStatus.setChecked(device.getStatus());
        holder.binding.tvDeviceRoom.setText(device.getRoom());

    }
    @Override
    public int getItemCount() {
        return devices.size();
    }
}
