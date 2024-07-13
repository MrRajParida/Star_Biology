package com.example.logregpanel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class TeacherLogin extends AppCompatActivity {
    private TextInputEditText userId, password;
    private TextInputLayout userIdLayout, passwordLayout;
    private Button techLoginBtn, registerBtn;  // Ensure this is MaterialButton
    private ProgressDialog progressDialog;
    private DatabaseReference teachersRef, registrationRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_login);

        userId = findViewById(R.id.teacherLoginUserId);
        password = findViewById(R.id.teacherLoginPassword);
        userIdLayout = findViewById(R.id.teacherUserIdRegInputLayout);
        passwordLayout = findViewById(R.id.teacherPasswordRegInputLayout);
        techLoginBtn = findViewById(R.id.teacherLoginBtn);
        registerBtn = findViewById(R.id.techRegBtn);// Ensure this matches the XML

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        teachersRef = FirebaseDatabase.getInstance().getReference("TEACHERS");
        registrationRef = FirebaseDatabase.getInstance().getReference("active/teacherRegister");

        techLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginTeacher();
            }
        });



        registrationRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean isRegisterEnabled = snapshot.getValue(Boolean.class);
                    if (isRegisterEnabled != null && isRegisterEnabled) {
                        registerBtn.setEnabled(true);
                        registerBtn.setText("Didn't have account create here?");
                    } else {
                        registerBtn.setEnabled(false);
                        registerBtn.setText("Registration time period is over");
                    }
                } else {
                    registerBtn.setEnabled(false);
                    registerBtn.setText("Registration time period is over");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
                registerBtn.setEnabled(false);
                registerBtn.setText("Registration time period is over");
            }
        });

        // Optional: Set a click listener for the register button
        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle button click
                // For example, navigate to registration activity
                startActivity(new Intent(TeacherLogin.this, TeacherRegister.class));
            }
        });

    }

    private void loginTeacher() {
        String userName = userId.getText().toString().trim();
        String userPass = password.getText().toString().trim();

        if (userName.isEmpty()) {
            userIdLayout.setError("Please enter your User ID");
            userId.requestFocus();
            return;
        }

        if (userPass.isEmpty()) {
            passwordLayout.setError("Please enter your password");
            password.requestFocus();
            return;
        }

        setComponentsEnabled(false); // Disable components while logging in
        progressDialog.show();

        teachersRef.orderByChild("username").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    DataSnapshot teacherSnapshot = dataSnapshot.getChildren().iterator().next();
                    String storedPassword = teacherSnapshot.child("password").getValue(String.class);
                    Boolean verified = teacherSnapshot.child("verified").getValue(Boolean.class);

                    if (storedPassword != null && storedPassword.equals(userPass)) {
                        if (verified != null && verified) {
                            progressDialog.dismiss();
                            setComponentsEnabled(true);
                            SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                            SharedPreferences.Editor editor = sharedPreferences.edit();
                            editor.putString("userRole", "teacher");
                            editor.apply();
                            Toast.makeText(TeacherLogin.this, "Login successful", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(TeacherLogin.this, TeacherHomeActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            progressDialog.dismiss();
                            setComponentsEnabled(true); // Re-enable components
                            Toast.makeText(TeacherLogin.this, "Your account is not verified yet", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        progressDialog.dismiss();
                        setComponentsEnabled(true); // Re-enable components
                        Toast.makeText(TeacherLogin.this, "Invalid User ID or Password", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressDialog.dismiss();
                    setComponentsEnabled(true); // Re-enable components
                    Toast.makeText(TeacherLogin.this, "User ID not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressDialog.dismiss();
                setComponentsEnabled(true); // Re-enable components
                Toast.makeText(TeacherLogin.this, "Database Error: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setComponentsEnabled(boolean enabled) {
        userId.setEnabled(enabled);
        password.setEnabled(enabled);
        techLoginBtn.setEnabled(enabled);
    }
}