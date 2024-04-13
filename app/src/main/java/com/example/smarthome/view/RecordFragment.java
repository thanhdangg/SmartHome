package com.example.smarthome.view;

import android.content.pm.PackageManager;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Path;
import android.media.MediaRecorder;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Locale;
import android.util.Log;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import android.Manifest;
import com.example.smarthome.R;
import com.example.smarthome.databinding.FragmentRecordBinding;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

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

        binding.btnRecord.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    startRecording();
                    binding.txtListening.setText("Đang nghe. . .");
                    binding.btnRecord.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
                    binding.imgRecord.setVisibility(View.VISIBLE);
                    binding.txtTryAgain.setVisibility(View.INVISIBLE);

                } else if (event.getAction() == MotionEvent.ACTION_UP) {
                    stopRecording();
                    binding.txtListening.setText("Thử lại");
                    binding.btnRecord.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#D3D3D3")));
                    binding.imgRecord.setVisibility(View.INVISIBLE);
                    binding.txtTryAgain.setVisibility(View.VISIBLE);
                }
                return false;
            }
        });
        binding.btnRecord.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRecording();
                binding.txtListening.setText("Đang nghe. . .");
                binding.btnRecord.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FF0000")));
                binding.imgRecord.setVisibility(View.VISIBLE);
                binding.txtTryAgain.setVisibility(View.INVISIBLE);
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
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        fileName = getActivity().getExternalCacheDir().getAbsolutePath();
        fileName += "/audiorecordtest.3gp";
        recorder.setOutputFile(fileName);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            recorder.prepare();
        } catch (IOException e) {
            Log.e("Recording", "prepare() failed");
        }
        recorder.start();
        isRecording = true;

    }

    private void stopRecording() {
        if (isRecording) {
            recorder.stop();
            recorder.release();
            recorder = null;
            isRecording = false;
        }
        try (Source source = Okio.source(new File(fileName))) {
            ByteString byteString = Okio.buffer(source).readByteString();
            String result = byteString.utf8();
            binding.txtResult.setText(result);
            binding.txtResult.setVisibility(View.VISIBLE);
        } catch (IOException e) {
            Log.e("Recording", "Failed to read audio file", e);
        }


        File file = new File(fileName);
        if (file.delete()) {
            Log.d("Recording", "Audio file deleted successfully");
        } else {
            Log.e("Recording", "Failed to delete audio file");
        }

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "vi-VN");
        startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);


    }
}