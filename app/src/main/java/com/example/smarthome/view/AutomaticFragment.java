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
import com.example.smarthome.viewmodel.WebSocketManager;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class AutomaticFragment extends Fragment {

    private FragmentAutomaticBinding binding;
    private WebSocketClient mWebSocketClient;
    private boolean isAutoLightOn = false;
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
        mWebSocketClient = WebSocketManager.getInstance().getWebSocketClient();
        binding.lnTurnOnLightWhenOpenDoor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isAutoLightOn = true;
                Toast.makeText(getContext().getApplicationContext(), "Đã cài đặt, đèn sẽ bật khi cửa mở", Toast.LENGTH_SHORT).show();

            }
        });

        binding.lnTunrnOnAllLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebSocketClient.send("BDPK");
                mWebSocketClient.send("BDPN");
                Toast.makeText(getContext().getApplicationContext(), "Đã bật tất cả đèn", Toast.LENGTH_SHORT).show();
            }
        });
        binding.lnTurnOffAllLight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mWebSocketClient.send("TDPK");
                mWebSocketClient.send("TDPN");
                Toast.makeText(getContext().getApplicationContext(), "Đã tắt tất cả đèn", Toast.LENGTH_SHORT).show();
            }
        });
        binding.lnTurnOnLightWhenMove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getContext().getApplicationContext(), "Đã cài đặt, sẽ bật đèn khi có chuyển động", Toast.LENGTH_SHORT).show();
            }
        });
    }


}