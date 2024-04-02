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
//                        Toast.makeText(getActivity(), "WebSocket Opened", Toast.LENGTH_SHORT).show();
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