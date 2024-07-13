package com.example.logregpanel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class TeacherRegister extends AppCompatActivity {
    private TextInputEditText nameEditText, emailEditText, passwordEditText;
    private TextInputLayout nameLayout, emailLayout, passwordLayout;
    private Button registerBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference teachersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_register);

        mAuth = FirebaseAuth.getInstance();
        teachersRef = FirebaseDatabase.getInstance().getReference("TEACHERS");

        nameEditText = findViewById(R.id.regUserName);
        emailEditText = findViewById(R.id.regEmail);
        passwordEditText = findViewById(R.id.regPassword);
        nameLayout = findViewById(R.id.teacherRegNameInput);
        emailLayout = findViewById(R.id.teacherRegEmailInput);
        passwordLayout = findViewById(R.id.teacherRegPasswordInput);
        registerBtn = findViewById(R.id.regBtn);

        registerBtn.setOnClickListener(v -> registerTeacher());
    }

    private void registerTeacher() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (name.isEmpty()) {
            nameLayout.setError("Please enter your name");
            nameEditText.requestFocus();
            return;
        }

        if (email.isEmpty()) {
            emailLayout.setError("Please enter your email");
            emailEditText.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordLayout.setError("Please enter your password");
            passwordEditText.requestFocus();
            return;
        }

        String username = name.substring(0, 4).toUpperCase() + getLastFourCharacters(email.split("@")[0]);

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            String userId = user.getUid();
                            Teacher teacher = new Teacher(name, email, password, username, false);
                            teachersRef.child(userId).setValue(teacher);

                            user.sendEmailVerification().addOnCompleteListener(task1 -> {
                                if (task1.isSuccessful()) {
                                    Intent intent = new Intent(TeacherRegister.this, EmailVerificationActivity.class);
                                    intent.putExtra("userType", "teacher");
                                    intent.putExtra("userId", username);  // Pass username to EmailVerificationActivity
                                    intent.putExtra("email", email);   // Pass email to EmailVerificationActivity
                                    startActivity(intent);
                                    finish();
                                } else {
                                    Toast.makeText(TeacherRegister.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                    } else {
                        Toast.makeText(TeacherRegister.this, "Registration failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private String getLastFourCharacters(String str) {
        return str.length() >= 4 ? str.substring(str.length() - 4).toUpperCase() : str.toUpperCase();
    }

    public static class Teacher {
        public String name;
        public String email;
        public String password;
        public String username;
        public boolean verified;

        public Teacher() {
            // Default constructor required for calls to DataSnapshot.getValue(Teacher.class)
        }

        public Teacher(String name, String email, String password, String username, boolean verified) {
            this.name = name;
            this.email = email;
            this.password = password;
            this.username = username;
            this.verified = verified;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        public String getPassword() {
            return password;
        }

        public void setPassword(String password) {
            this.password = password;
        }

        public boolean isVerified() {
            return verified;
        }

        public void setVerified(boolean verified) {
            this.verified = verified;
        }

        public String getUsername() {
            return username;
        }

        public void setUsername(String username) {
            this.username = username;
        }
    }
}