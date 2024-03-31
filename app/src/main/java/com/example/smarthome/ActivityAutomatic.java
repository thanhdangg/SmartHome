package com.example.smarthome;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.smarthome.databinding.ActivityAutomaticBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ActivityAutomatic extends AppCompatActivity {

    private ActivityAutomaticBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAutomaticBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        EdgeToEdge.enable(this);
//        setContentView(R.layout.activity_automatic);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        binding.bottomNavigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.nav_home) {
                    Intent intent = new Intent(ActivityAutomatic.this, MainActivity.class);
                    startActivity(intent);
                } else if (itemId == R.id.nav_record) {
                    record();
                } else if (itemId == R.id.nav_automatic) {

                }
                return true;
            }
        });

    }
    private void record() {

    }

}