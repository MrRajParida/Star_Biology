package com.antech.starbiologyedu.Activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.starproduction.starbiology.R;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_splash);

        ImageView introGif = findViewById(R.id.logo);

        // Load GIF using Glide
        Glide.with(this).asBitmap().load(R.mipmap.ic_launcher).into(introGif);

        new Handler().postDelayed(() -> {
            checkUserLoginStatus();
        }, 5000);
    }

    private void checkUserLoginStatus() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            navigateToHomeActivity();
        } else {
            navigateToLoginActivity();
        }
    }
    private void navigateToHomeActivity() {
        Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
    private void navigateToLoginActivity() {
        Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }
}




