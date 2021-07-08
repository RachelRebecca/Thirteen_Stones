package com.example.thirteen_stones.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import com.example.thirteen_stones.R;

import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_AUTO_BATTERY;
import static androidx.appcompat.app.AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        AppCompatDelegate.setDefaultNightMode
                (Build.VERSION.SDK_INT < 28 ? MODE_NIGHT_AUTO_BATTERY : MODE_NIGHT_FOLLOW_SYSTEM);
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}