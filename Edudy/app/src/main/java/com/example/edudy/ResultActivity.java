package com.example.edudy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import java.util.Objects;

public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
        TextView scored = findViewById(R.id.scored);
        TextView total = findViewById(R.id.total_btn);
        Button donebtn = findViewById(R.id.done_btn);

        scored.setText(String.valueOf(getIntent().getIntExtra("score", 0)));
        total.setText("OUT OF " + getIntent().getIntExtra("total", 0));
        donebtn.setOnClickListener(view -> finish());
    }
}