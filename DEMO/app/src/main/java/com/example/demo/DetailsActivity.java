package com.example.demo;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class DetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_details);

        ImageView img = findViewById(R.id.getImg);
        TextView name = findViewById(R.id.getName);
        TextView email = findViewById(R.id.getEmail);

        String iName = getIntent().getStringExtra("name");
        String iEmail = getIntent().getStringExtra("email");
        int iImg = getIntent().getIntExtra("img", -1);

        img.setImageResource(iImg);
        name.setText(iName);
        email.setText(iEmail);

    }
}