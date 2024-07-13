package com.example.logregpanel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class PrincipalLogin extends AppCompatActivity {
    private Button headLogin, headLoginToRegisterBtn;
    private DatabaseReference databaseReference;
    private TextInputEditText loginUserId, loginPassword;
    private TextInputLayout userIdLayout, passwordLayout;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef; // Reference to USER node
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_principal_login);

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("PRINCIPAL");
        loginUserId = findViewById(R.id.headLoginUserId);
        userIdLayout = findViewById(R.id.headUserIdLogInputLayout);
        loginPassword = findViewById(R.id.headLoginPassword);
        passwordLayout = findViewById(R.id.headPasswordLogInputLayout);
        headLogin = findViewById(R.id.headLoginBtn);
        headLoginToRegisterBtn = findViewById(R.id.headLoginToRegisterBtn);

        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Logging in...");
        progressDialog.setCancelable(false);

        // Set up login button click listener
        headLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loginUser();
            }
        });

        // Initialize Firebase Database
        databaseReference = FirebaseDatabase.getInstance().getReference("active/headRegister"); // Change this to your actual path

        // Fetch the value from Firebase
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Boolean isRegisterEnabled = snapshot.getValue(Boolean.class);
                    if (isRegisterEnabled != null && isRegisterEnabled) {
                        headLoginToRegisterBtn.setEnabled(true);
                        headLoginToRegisterBtn.setText("Didn't have account create here?");
                    } else {
                        headLoginToRegisterBtn.setEnabled(false);
                        headLoginToRegisterBtn.setText("You are not allow to create");
                    }
                } else {
                    headLoginToRegisterBtn.setEnabled(false);
                    headLoginToRegisterBtn.setText("You are not allow to create");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle possible errors.
                headLoginToRegisterBtn.setEnabled(false);
                headLoginToRegisterBtn.setText("You are not allow to create");
            }
        });

        // Optional: Set a click listener for the button
        headLoginToRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Handle button click
                // For example, navigate to registration activity
                startActivity(new Intent(PrincipalLogin.this, PrincipalRegister.class));
            }
        });
    }

    private void loginUser() {
        String userId = loginUserId.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        if (userId.isEmpty()) {
            userIdLayout.setError("Please enter your UserId");
            loginUserId.requestFocus();
            return;
        }

        if (password.isEmpty()) {
            passwordLayout.setError("Please enter your password");
            loginPassword.requestFocus();
            return;
        }
        setComponentsEnabled(false);
        progressDialog.show();

        userRef.orderByChild("department_Username").equalTo(userId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                    String email = userSnapshot.child("department_Email").getValue(String.class);
                    Boolean verified = userSnapshot.child("verified").getValue(Boolean.class);

                if (email != null) {
                    mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                if (user != null) {
                                    // Check email verification status
                                    if (user.isEmailVerified() && Boolean.TRUE.equals(verified)) {
                                        SharedPreferences sharedPreferences = getSharedPreferences("LoginPrefs", MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("userRole", "principal");
                                        editor.apply();
                                        Intent intent = new Intent(PrincipalLogin.this, HeadActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                    } else {
                                        Toast.makeText(PrincipalLogin.this, "Please verify your email before logging in.", Toast.LENGTH_LONG).show();
                                        progressDialog.dismiss();
                                    }
                                }
                            } else {
                                Toast.makeText(PrincipalLogin.this, "Login failed: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                            }
                            setComponentsEnabled(true);
                            progressDialog.dismiss();
                        }
                    });
                } else {
                    Toast.makeText(PrincipalLogin.this, "UserId not found", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                    setComponentsEnabled(true);
                }
            } else {
                Toast.makeText(PrincipalLogin.this, "UserId not found", Toast.LENGTH_SHORT).show();
                progressDialog.dismiss();
                setComponentsEnabled(true);
            }
        }

        @Override
        public void onCancelled(@NonNull DatabaseError error) {
            Toast.makeText(PrincipalLogin.this, "Error retrieving UserId", Toast.LENGTH_SHORT).show();
            setComponentsEnabled(true);
            progressDialog.dismiss();
        }
    });
}

private void setComponentsEnabled(boolean enabled) {
    loginUserId.setEnabled(enabled);
    loginPassword.setEnabled(enabled);
    headLogin.setEnabled(enabled);
    }
}
