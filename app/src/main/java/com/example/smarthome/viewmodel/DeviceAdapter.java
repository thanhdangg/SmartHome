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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
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
//    private String webSocketUrl = "wss://ba51-3-25-190-4.ngrok-free.app" ;
    private String webSocketUrl = "" ;


    static {
        DEVICE_TYPE_ABBREVIATIONS.put("light", "D");
        DEVICE_TYPE_ABBREVIATIONS.put("lamp", "D");
        DEVICE_TYPE_ABBREVIATIONS.put("ceilingfan", "Q");
        DEVICE_TYPE_ABBREVIATIONS.put("fan", "Q");

        ROOM_ABBREVIATIONS.put("Phòng khách", "PK");
        ROOM_ABBREVIATIONS.put("Phòng ngủ", "PN");
    }
    public DeviceAdapter(List<Device> devices, Context context) {
        this.devices = devices;
        this.context = context;
        connectWebSocket();
    }
    public DeviceAdapter(List<Device> devices, Context context, String webSocketUrl) {
        this.devices = devices;
        this.context = context;
        this.webSocketUrl = webSocketUrl;
        Log.d("DeviceAdapter", "WebSocket URL: " + webSocketUrl);
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
    private void connectWebSocket() {
        URI uri;
        try {
            uri = new URI(webSocketUrl);
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
                        Toast.makeText(context, "Kết nối WebSocket thành công", Toast.LENGTH_SHORT).show();
                    }
                });
                Log.d("DeviceAdapter", "Kết nối WebSocket thành công" + webSocketUrl);
            }
            @Override
            public void onTextReceived(String s) {
                Log.d("DeviceAdapter", "onTextReceived: " + s);
                updateDeviceStatus(s);

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
                Log.d("DeviceAdapter", "Kết nối WebSocket thất bại" + e.toString() );
            }

            @Override
            public void onCloseReceived(int i, String s) {
                //Ghi cái log vo đay cai
                Log.d("DeviceAdapter", "Ngawts Kết nối WebSocket thành công" + webSocketUrl);

            }
        };
        mWebSocketClient.connect();
    }
    private void updateDeviceStatus(String message) {
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
    private static String extractWssValue(String jsonResponse) {
        int startIndex = jsonResponse.indexOf("\"wss\":") + "\"wss\":".length();
        int endIndex = jsonResponse.indexOf(",", startIndex);
        if (endIndex == -1) {
            endIndex = jsonResponse.indexOf("}", startIndex);
        }
        return jsonResponse.substring(startIndex, endIndex);
    }
    private static String getWebSocketUrl(){
        String url = "http://3.25.190.4:1911/wss";
        String wssValue = "";
        try {
            URL apiUrl = new URL(url);

            HttpURLConnection conn = (HttpURLConnection) apiUrl.openConnection();
            conn.setRequestMethod("GET");

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();

            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            conn.disconnect();

            String jsonResponse = response.toString();
            wssValue = extractWssValue(jsonResponse);
            Log.d("DeviceAdapter", "Value of 'wss' key: " + wssValue); // Add this line
        } catch (IOException e) {
            e.printStackTrace();
        }
        return wssValue;
    }

}
