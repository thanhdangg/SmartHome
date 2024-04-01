package com.example.smarthome.view;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.example.smarthome.R;

import com.example.smarthome.databinding.FragmentHomeBinding;

import com.example.smarthome.model.OpenWeatherMapService;
import com.example.smarthome.model.WeatherResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.Call;

import retrofit2.converter.gson.GsonConverterFactory;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;
    private Retrofit retrofit;
    private String[] rooms = {"My Home", "Office"};

    public HomeFragment() {
    }

    public static HomeFragment newInstance(String param1, String param2) {
        HomeFragment fragment = new HomeFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = FragmentHomeBinding.inflate(getLayoutInflater());
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(logging)
                .build();
        retrofit = new Retrofit.Builder()
                .baseUrl("http://api.openweathermap.org/")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        OpenWeatherMapService service = retrofit.create(OpenWeatherMapService.class);
        Call<WeatherResponse> call = service.getCurrentWeatherData("16.0753691", "108.1470655", "7a6fbf885d844274d608a95135dfcd70");
        call.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {
                if (response.isSuccessful()) {
                    WeatherResponse weatherResponse = response.body();
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            binding.tvTemp.setText(String.valueOf(weatherResponse.getMain().getTemp()));
                            binding.tvTempMin.setText(String.valueOf(weatherResponse.getMain().getTempMin()));
                            binding.tvTempMax.setText(String.valueOf(weatherResponse.getMain().getTempMax()));
                            binding.tvHumidity.setText(String.valueOf(weatherResponse.getMain().getHumidity()));
                            binding.tvClouds.setText(String.valueOf(weatherResponse.getClouds().getAll()));
                            binding.tvCity.setText(weatherResponse.getName());

                            if (!weatherResponse.getWeather().isEmpty()) {
                                binding.tvWeatherDescription.setText(weatherResponse.getWeather().get(0).getDescription());

                                String icon = weatherResponse.getWeather().get(0).getIcon();
                                String iconUrl = "http://openweathermap.org/img/w/" + icon + ".png";
                                Log.d("Weather", "Icon URL : "+ iconUrl);
                                Picasso.get().load(iconUrl).into(binding.ivWeatherIcon, new com.squareup.picasso.Callback() {
                                    @Override
                                    public void onSuccess() {
                                        Log.d("Picasso", "Icon URL : "+ iconUrl);
                                        Log.d("Picasso", "Image load success");
                                    }
                                    @Override
                                    public void onError(Exception e) {
                                        binding.ivWeatherIcon.setImageResource(R.drawable.may);
                                        Log.d("Picasso", "Icon URL : "+ iconUrl);
                                        Log.e("Picasso", "Image load error", e);
                                    }
                                });
                            }
                        } // Đây là nơi dấu ngoặc đơnng } nên được đặt
                    });
                } else {
                    Log.d("Weather", "Error : "+response.code()+" - "+response.message());
                }
            }
            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {
                Log.e("Weather", "Error : "+t.getMessage());
            }
        });

        ArrayAdapter<String> adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_item, rooms);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.spinnerRooms.setAdapter(adapter);

        binding.spinnerRooms.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedRoom = (String) parent.getItemAtPosition(position);
                Toast.makeText(getActivity(), selectedRoom, Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        binding.btnMe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Fragment selectedFragment = new MeFragment();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .replace(R.id.fragment_container, selectedFragment)
                        .commit();
            }
        });
    }
}