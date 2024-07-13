package com.example.logregpanel;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class HeadActivity extends AppCompatActivity {
    private FloatingActionButton teacherAdd, studentAdd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_head);

        teacherAdd = findViewById(R.id.addTeacher);
        teacherAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HeadActivity.this, TeacherRegister.class));
            }
        });

        studentAdd = findViewById(R.id.addStudent);
        studentAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HeadActivity.this, StudentRegister.class));
            }
        });

    }
}