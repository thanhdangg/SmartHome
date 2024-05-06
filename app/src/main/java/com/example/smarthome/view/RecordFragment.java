package com.example.smarthome.view;

import android.content.pm.PackageManager;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Path;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
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
import com.example.smarthome.R;
import com.example.smarthome.databinding.FragmentRecordBinding;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


import okhttp3.*;
import okio.ByteString;
import okio.Okio;
import okio.Source;


public class RecordFragment extends Fragment {

    private FragmentRecordBinding binding;
    private MediaRecorder recorder;
    private String fileName = null;
    private static final int REQUEST_CODE_SPEECH_INPUT = 1;
    private static final int REQUEST_RECORD_AUDIO_PERMISSION = 200;
    private static final int RESULT_OK = 1;
    private boolean isRecording = false;
    private final OkHttpClient httpClient = new OkHttpClient();

    private MediaPlayer player = null;





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
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, REQUEST_RECORD_AUDIO_PERMISSION);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentRecordBinding.inflate(inflater, container, false);

        binding.btnRecord.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startRecording();
                binding.txtListening.setText("Đang nghe. . .");
                binding.btnRecord.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
                binding.imgRecord.setVisibility(View.VISIBLE);
                binding.txtTryAgain.setVisibility(View.INVISIBLE);
                return true; // return true to indicate that the event is consumed
            }
        });

        binding.btnRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    stopRecording();
                    binding.txtListening.setText("Thử lại");
                    binding.btnRecord.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
                    binding.imgRecord.setVisibility(View.INVISIBLE);
                    binding.txtTryAgain.setVisibility(View.VISIBLE);
                }
                return false; // return false to let the event propagate to the long click listener
            }
        });
        binding.btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        return binding.getRoot();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            binding.txtResult.setText(result.get(0));
            binding.txtResult.setVisibility(View.VISIBLE);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == REQUEST_RECORD_AUDIO_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted
            } else {
                // Permission denied
            }
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void startRecording() {
        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            fileName = getActivity().getExternalCacheDir().getAbsolutePath();
            fileName += "/audiorecordtest.wav";
            recorder.setOutputFile(fileName);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
            recorder.prepare();
            recorder.start();
            isRecording = true;
        } catch (IOException e) {
            Log.e("Recording", "prepare() failed", e);
        }
    }

    private void stopRecording() {
        if (isRecording && recorder != null) {
            try {
                recorder.stop();
            } catch (RuntimeException e) {
                Log.e("Recording", "stop() failed", e);
            }
            recorder.release();
            recorder = null;
            isRecording = false;
        }
        if (fileName != null) {
            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    player = new MediaPlayer();
                    try {
                        player.setDataSource(fileName);
                        player.prepare();
                        player.start();
                    } catch (IOException e) {
                        Log.e("Playback", "prepare() failed", e);
                    }
                }
            }, 1000);

            File file = new File(fileName);
            if (file.exists()) {
                try {
                    RequestBody requestBody = RequestBody.create(MediaType.parse("audio/wav"), file);
                    MultipartBody.Part filePart = MultipartBody.Part.createFormData("file", file.getName(), requestBody);
                    MultipartBody multipartBody = new MultipartBody.Builder()
                            .setType(MultipartBody.FORM)
                            .addPart(filePart)
                            .build();
                    Request request = new Request.Builder()
                            .url("http://3.25.190.4:1911/wss")
                            .post(multipartBody)
                            .build();
                    httpClient.newCall(request).enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            Log.e("Recording", "POST request failed", e);
                        }
                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            if (response.isSuccessful()) {
                                final String myResponse = response.body().string();
                                getActivity().runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        binding.txtResult.setText(myResponse);
                                        binding.txtResult.setVisibility(View.VISIBLE);
                                    }
                                });
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e("Recording", "Failed to encode audio file", e);
                }

//                if (file.delete()) {
//                    Log.d("Recording", "Audio file deleted successfully");
//                } else {
//                    Log.e("Recording", "Failed to delete audio file");
//                }
            } else {
                Log.e("Recording", "Audio file does not exist");
            }
        } else {
            Log.e("Recording", "File name is null");
        }
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }
}