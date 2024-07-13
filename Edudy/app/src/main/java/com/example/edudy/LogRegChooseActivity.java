package com.example.edudy;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LogRegChooseActivity extends AppCompatActivity {
    private Button mainLogin, mainSignup;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_reg_choose);

        mainLogin = findViewById(R.id.mainLogin_button);
        mainLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LogRegChooseActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        mainSignup = findViewById(R.id.mainNeeds_new_account);
        mainSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(LogRegChooseActivity.this, RegisterActivity.class);
                startActivity(intent1);
                finish();
            }
        });


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}