package com.example.smarthome.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.smarthome.databinding.DeviceItemBinding;
import com.example.smarthome.model.Device;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.gustavoavila.websocketclient.WebSocketClient;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {
    private List<Device> devices;
    private WebSocketClient mWebSocketClient;
    private Context context;
    private static final Map<String, String> DEVICE_TYPE_ABBREVIATIONS = new HashMap<>();
    private static final Map<String, String> ROOM_ABBREVIATIONS = new HashMap<>();
    private String webSocketUrl;


    static {
        DEVICE_TYPE_ABBREVIATIONS.put("light", "D");
        DEVICE_TYPE_ABBREVIATIONS.put("lamp", "D");
        DEVICE_TYPE_ABBREVIATIONS.put("ceilingfan", "Q");
        DEVICE_TYPE_ABBREVIATIONS.put("fan", "Q");

        ROOM_ABBREVIATIONS.put("Phòng khách", "PK");
        ROOM_ABBREVIATIONS.put("Phòng ngủ", "PN");
    }
    public DeviceAdapter(List<Device> devices) {
        this.devices = devices;
    }
    public DeviceAdapter(List<Device> devices, Context context) {
        this.devices = devices;
        this.context = context;
        mWebSocketClient = WebSocketManager.getInstance().getWebSocketClient();
    }
    public DeviceAdapter(List<Device> devices, Context context, String webSocketUrl) {
        this.devices = devices;
        this.context = context;
        this.webSocketUrl = webSocketUrl;
        mWebSocketClient = WebSocketManager.getInstance().getWebSocketClient();
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
        holder.binding.swDeviceStatus.setChecked(device.getStatus());
        holder.binding.tvDeviceRoom.setText(device.getRoom());

        holder.binding.swDeviceStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                String deviceTypeAbbreviation = DEVICE_TYPE_ABBREVIATIONS.get(device.getIconPath());
                String roomAbbreviation = ROOM_ABBREVIATIONS.get(device.getRoom());
                String statusAbbreviation = isChecked ? "B" : "T";
                String command = statusAbbreviation + deviceTypeAbbreviation + roomAbbreviation;
                if (mWebSocketClient != null) {
                    if (deviceTypeAbbreviation != null && roomAbbreviation != null) {
                        mWebSocketClient.send(command);
                        Log.d("DeviceAdapter", "onCheckedChanged: " + command);
                    }
                }
                else{
                    Toast.makeText(holder.itemView.getContext(), "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
                    if (isChecked) {
                        Toast.makeText(holder.itemView.getContext(), device.getName() + " đã bật", Toast.LENGTH_SHORT).show();
                        Log.d("DeviceAdapter", "onCheckedChanged: " + device.getName() + " đã bật");
                    } else {
                        Toast.makeText(holder.itemView.getContext(), device.getName() + " đã tắt", Toast.LENGTH_SHORT).show();
                        Log.d("DeviceAdapter", "onCheckedChanged: " + device.getName() + " đã tatws");
                    }
                }
            }
        });
    }
    @Override
    public int getItemCount() {
        return devices.size();
    }
    public void updateDeviceStatus(String message) {
        String statusAbbreviation = message.substring(0, 1);
        String deviceTypeAbbreviation = message.substring(1, 2);
        String roomAbbreviation = message.substring(2, 4);

        boolean status = statusAbbreviation.equals("B");

        for (Device device : devices) {
            String deviceType = DEVICE_TYPE_ABBREVIATIONS.get(device.getIconPath());
            String room = ROOM_ABBREVIATIONS.get(device.getRoom());
            if (deviceType != null && room != null && deviceType.equals(deviceTypeAbbreviation) && room.equals(roomAbbreviation)) {
                device.setStatus(status);
                break;
            }
        }

        ((Activity) context).runOnUiThread(new Runnable() {
            @Override
            public void run() {
                notifyDataSetChanged();
            }
        });
    }
}
