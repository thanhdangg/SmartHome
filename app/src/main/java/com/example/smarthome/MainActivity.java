package com.example.smarthome;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;

import com.example.smarthome.databinding.ActivityMainBinding;
import com.example.smarthome.model.Device;
import com.example.smarthome.view.HomeFragment;
import com.example.smarthome.view.RecordFragment;
import com.example.smarthome.view.AutomaticFragment;
import com.example.smarthome.view.WebSocketUrlProvider;
import com.example.smarthome.view.WebSocketUrlResponse;
import com.example.smarthome.viewmodel.DeviceAdapter;
import com.example.smarthome.viewmodel.DeviceStatusUpdater;
import com.example.smarthome.viewmodel.WebSocketManager;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dev.gustavoavila.websocketclient.WebSocketClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private WebSocketClient mWebSocketClient;
    private WebSocketUrlProvider webSocketUrlProvider;
    private List<Device> devices;
    private Context context;
    private DeviceAdapter deviceAdapter;


    private static final Map<String, String> DEVICE_TYPE_ABBREVIATIONS = new HashMap<>();
    private static final Map<String, String> ROOM_ABBREVIATIONS = new HashMap<>();


    static {
        DEVICE_TYPE_ABBREVIATIONS.put("light", "D");
        DEVICE_TYPE_ABBREVIATIONS.put("lamp", "D");
        DEVICE_TYPE_ABBREVIATIONS.put("ceilingfan", "Q");
        DEVICE_TYPE_ABBREVIATIONS.put("fan", "Q");

        ROOM_ABBREVIATIONS.put("Phòng khách", "PK");
        ROOM_ABBREVIATIONS.put("Phòng ngủ", "PN");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        deviceAdapter = new DeviceAdapter(devices, this);

        webSocketUrlProvider = new WebSocketUrlProvider();  // Initialize your WebSocketUrlProvider
        getWebSocketUrlAndConnect();

        EdgeToEdge.enable(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right,0);
            return insets;
        });
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new HomeFragment())
                .commit();
        binding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int itemId = item.getItemId();
            if (itemId == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (itemId == R.id.nav_record) {
                selectedFragment = new RecordFragment();
            } else if (itemId == R.id.nav_automatic) {
                selectedFragment = new AutomaticFragment();
            }
            if (selectedFragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
            return true;
        });

    }
    private void getWebSocketUrlAndConnect() {
        webSocketUrlProvider.getWebSocketUrl(new Callback<WebSocketUrlResponse>() {
            @Override
            public void onResponse(Call<WebSocketUrlResponse> call, Response<WebSocketUrlResponse> response) {
                if (response.isSuccessful()) {
                    String webSocketUrl = response.body().getWssUrl();
                    connectWebSocket(webSocketUrl);
                }
            }

            @Override
            public void onFailure(Call<WebSocketUrlResponse> call, Throwable t) {
                Log.e("MainActivity", "Failed to get WebSocket URL: " + t.getMessage());
            }
        });
    }
    private void connectWebSocket(String webSocketUrl) {
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
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        WebSocketManager.getInstance().setWebSocketClient(mWebSocketClient);
                    }
                });
            }
            @Override
            public void onTextReceived(String s) {
                Log.d("WebSocketClient", "onTextReceived: " + s);
                Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
                if (fragment instanceof DeviceStatusUpdater) {
                    ((DeviceStatusUpdater) fragment).updateDeviceStatus(s);
                }
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
                Log.d("WebSocketClient", "Kết nối WebSocket thất bại" + e.toString() );
            }
            @Override
            public void onCloseReceived(int i, String s) {
                Log.d("WebSocketClient", "Ngắt Kết nối WebSocket" + webSocketUrl);
            }

        };
        mWebSocketClient.connect();
    }


}