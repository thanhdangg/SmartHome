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

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import dev.gustavoavila.websocketclient.WebSocketClient;

public class DeviceAdapter extends RecyclerView.Adapter<DeviceAdapter.DeviceViewHolder> {

    private List<Device> devices;
    private WebSocketClient mWebSocketClient;
    private Context context;

    public DeviceAdapter(List<Device> devices, Context context) {
        this.devices = devices;
        this.context = context;
        connectWebSocket();
    }

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

        holder.binding.swDeviceStatus.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mWebSocketClient != null) {
                    if (isChecked) {
                        Toast.makeText(holder.itemView.getContext(), device.getName() + " đã bật", Toast.LENGTH_SHORT).show();
                        mWebSocketClient.send("Bat " + device.getId());

                    } else {
                        mWebSocketClient.send("Tat " + device.getId());
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

    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI("wss://silly-fox-89.telebit.io");
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen() {
                ((Activity) context).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }

            @Override
            public void onTextReceived(String s) {

            }

            @Override
            public void onBinaryReceived(byte[] bytes) {

            }

            @Override
            public void onPingReceived(byte[] bytes) {

            }

            @Override
            public void onPongReceived(byte[] bytes) {

            }

            @Override
            public void onException(Exception e) {

            }

            @Override
            public void onCloseReceived(int i, String s) {

            }
        };
        mWebSocketClient.connect();
    }
}
