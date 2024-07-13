package com.example.logregpanel;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class SplashActivity extends AppCompatActivity {
    private static final int SPLASH_SCREEN_DELAY = 2000;
    private FirebaseAuth mAuth;
    private DatabaseReference teachersRef;
    private DatabaseReference usersRef;
    private DatabaseReference principalRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        mAuth = FirebaseAuth.getInstance();
        teachersRef = FirebaseDatabase.getInstance().getReference("TEACHERS");
        usersRef = FirebaseDatabase.getInstance().getReference("USER");
        principalRef = FirebaseDatabase.getInstance().getReference("PRINCIPAL");

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                checkLoginStatus();
            }
        }, SPLASH_SCREEN_DELAY);
    }

    private void checkLoginStatus() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
        String userRole = sharedPreferences.getString("userRole", "");

        if (mAuth.getCurrentUser() != null) {
            String uid = mAuth.getCurrentUser().getUid();

            if (userRole.equals("teacher")) {
                teachersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Boolean verified = dataSnapshot.child("verified").getValue(Boolean.class);
                            if (verified != null && verified) {
                                navigateToActivity(TeacherHomeActivity.class);
                            } else {
                                navigateToActivity(TeacherLogin.class);
                            }
                        } else {
                            navigateToActivity(MainActivity.class);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(SplashActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (userRole.equals("student")) {
                usersRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Boolean verified = dataSnapshot.child("verified").getValue(Boolean.class);
                            if (verified != null && verified) {
                                navigateToActivity(StudentHomeActivity.class);
                            } else {
                                navigateToActivity(StudentLogin.class);
                            }
                        } else {
                            navigateToActivity(MainActivity.class);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(SplashActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else if (userRole.equals("principal")) {
                principalRef.child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            Boolean verified = dataSnapshot.child("verified").getValue(Boolean.class);
                            if (verified != null && verified) {
                                navigateToActivity(HeadActivity.class);
                            } else {
                                navigateToActivity(PrincipalLogin.class);
                            }
                        } else {
                            navigateToActivity(MainActivity.class);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        Toast.makeText(SplashActivity.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                navigateToActivity(MainActivity.class);
            }
        } else {
            navigateToActivity(MainActivity.class);
        }
    }

    private void navigateToActivity(Class<?> activityClass) {
        Intent intent = new Intent(SplashActivity.this, activityClass);
        startActivity(intent);
        finish();
    }
}