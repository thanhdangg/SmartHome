package com.example.smarthome.view;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.example.smarthome.databinding.FragmentAutomaticBinding;

import dev.gustavoavila.websocketclient.WebSocketClient;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.smarthome.R;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AutomaticFragment extends Fragment {

    private FragmentAutomaticBinding binding;
    private WebSocketClient mWebSocketClient;

    public AutomaticFragment() {

    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentAutomaticBinding.inflate(getLayoutInflater());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentAutomaticBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);



        connectWebSocket();

        // Set up listeners for each switch
        setupSwitchListener(binding.switchDen, 1);
        setupSwitchListener(binding.switchQuat, 2);
        setupSwitchListener(binding.switchTivi, 3);
        setupSwitchListener(binding.switchCua, 4);

        binding.switchDen.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mWebSocketClient.send("Bat 1");
                    Toast.makeText(getContext().getApplicationContext(), "Đèn đã bật", Toast.LENGTH_SHORT).show();

                } else {
                    mWebSocketClient.send("Tat 1");
                    Toast.makeText(getContext().getApplicationContext(), "Đèn đã tắt", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.switchQuat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mWebSocketClient.send("Bat 2");
                    Toast.makeText(getContext().getApplicationContext(), "Quạt đã bật", Toast.LENGTH_SHORT).show();

                } else {
                    mWebSocketClient.send("Tat 2");
                    Toast.makeText(getContext().getApplicationContext(), "Quạt đã tắt", Toast.LENGTH_SHORT).show();

                }
            }
        });
        binding.switchTivi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mWebSocketClient.send("Bat 3");
                    Toast.makeText(getContext().getApplicationContext(), "Tivi đã bật", Toast.LENGTH_SHORT).show();

                } else {
                    mWebSocketClient.send("Tat 3");
                    Toast.makeText(getContext().getApplicationContext(), "Tivi đã tắt", Toast.LENGTH_SHORT).show();
                }
            }
        });
        binding.switchCua.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mWebSocketClient.send("Bat 4");
                    Toast.makeText(getContext().getApplicationContext(), "Cửa đang mở", Toast.LENGTH_SHORT).show();

                } else {
                    mWebSocketClient.send("Tat 4");
                    Toast.makeText(getContext().getApplicationContext(), "Cửa đang đóng", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    private void setupSwitchListener(Switch switchView, int deviceId) {
        switchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    mWebSocketClient.send("Bat " + deviceId);
                    Toast.makeText(getContext().getApplicationContext(), "Thiết bị " + deviceId + " đã bật", Toast.LENGTH_SHORT).show();
                } else {
                    mWebSocketClient.send("Tat " + deviceId);
                    Toast.makeText(getContext().getApplicationContext(), "Thiết bị " + deviceId + " đã tắt", Toast.LENGTH_SHORT).show();
                }
            }
        });
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
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                    }
                });
            }

            @Override
            public void onTextReceived(String s) {
                updateSwitchStatus(s);
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
    private void updateSwitchStatus(String message) {
        // Parse message to get device ID and status
        // Example: if message is "Bat 1" or "Tat 1", ID is 1 and status is corresponding
        String[] parts = message.split(" ");
        String command = parts[0];
        int id = Integer.parseInt(parts[1]);
        boolean status = command.equals("Bat");

        // Find the corresponding switch and update its status
        Switch switchView;
        switch (id) {
            case 1:
                switchView = binding.switchDen;
                break;
            case 2:
                switchView = binding.switchQuat;
                break;
            case 3:
                switchView = binding.switchTivi;
                break;
            case 4:
                switchView = binding.switchCua;
                break;
            default:
                return;
        }
        switchView.setChecked(status);
    }

}