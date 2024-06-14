package com.example.smarthome.view;

import static android.app.Activity.RESULT_OK;

import android.content.ActivityNotFoundException;
import android.content.pm.PackageManager;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Path;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.util.Base64;
import android.util.Log;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.os.Handler;

import android.Manifest;
import android.widget.Toast;

import com.example.smarthome.R;
import com.example.smarthome.databinding.FragmentRecordBinding;
import com.example.smarthome.viewmodel.WebSocketManager;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


import dev.gustavoavila.websocketclient.WebSocketClient;
import okhttp3.*;
import okio.ByteString;
import okio.Okio;
import okio.Source;


public class RecordFragment extends Fragment {

    private FragmentRecordBinding binding;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int SPEECH_REQUEST_CODE = 0;

    private boolean permissionToRecordAccepted = false;
    private String[] permissions = {Manifest.permission.RECORD_AUDIO};
    private WebSocketClient mWebSocketClient;



    public RecordFragment() {
    }

    public static RecordFragment newInstance(String param1, String param2) {
        RecordFragment fragment = new RecordFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCompat.requestPermissions(getActivity(), permissions, REQUEST_RECORD_AUDIO_PERMISSION);
        mWebSocketClient = WebSocketManager.getInstance().getWebSocketClient();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecordBinding.inflate(inflater, container, false);
        binding.btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startVoiceRecognition();
                binding.txtListening.setText("Đang nghe. . .");
                binding.btnRecord.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
                binding.imgRecord.setVisibility(View.VISIBLE);
                binding.txtTryAgain.setVisibility(View.INVISIBLE);
            }
        });

        return binding.getRoot();
    }
    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak now...");

        try {
            startActivityForResult(intent, SPEECH_REQUEST_CODE);
        } catch (ActivityNotFoundException a) {
            Toast.makeText(getActivity(), "Your device doesn't support Speech Recognition", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK) {
            List<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String spokenText = results.get(0);
            binding.txtResult.setText(spokenText);
            binding.txtResult.setVisibility(View.VISIBLE);
            binding.txtListening.setVisibility(View.INVISIBLE);
            binding.imgRecord.setVisibility(View.INVISIBLE);

            Log.d("WebSocket", "Result: " + spokenText);
            String command = "";
            if (spokenText.contains("bật") || spokenText.contains("mở")) {
                command = command.concat("B");
            } else if (spokenText.contains("tắt")) {
                command = command.concat("B");
            } else {
                binding.txtTryAgain.setVisibility(View.VISIBLE);
            }
            if (spokenText.contains("đèn")) {
                command = command.concat("D");
            } else if (spokenText.contains("quạt")) {
                command = command.concat("Q");
            } else {
                binding.txtTryAgain.setVisibility(View.VISIBLE);
            }
            if (spokenText.contains("phòng khách")) {
                command = command.concat("PK");
            } else if (spokenText.contains("phòng ngủ")) {
                command = command.concat("PN");
            } else {
                binding.txtTryAgain.setVisibility(View.VISIBLE);
            }
            Log.d("WebSocket", "mWebSocketClient: "+  mWebSocketClient + " command "+ command);
            if (mWebSocketClient != null) {
                mWebSocketClient.send(command);
            }
            else {
                Toast.makeText(getActivity(), "Không thể kết nối đến server", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_RECORD_AUDIO_PERMISSION:
                permissionToRecordAccepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                break;
        }
        if (!permissionToRecordAccepted) getActivity().finish();
    }
}