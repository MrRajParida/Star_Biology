package com.example.logregpanel;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class EmailVerificationActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private DatabaseReference reference;
    private String userType, userEmail, uid;

    private TextView verificationMessage;
    private Button refreshButton, resendButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        verificationMessage = findViewById(R.id.verificationMessage);
        refreshButton = findViewById(R.id.refreshButton);
        resendButton = findViewById(R.id.resendButton);

        // Get intent extras
        userType = getIntent().getStringExtra("userType");
        userEmail = getIntent().getStringExtra("email");
        uid = getIntent().getStringExtra("uid");

        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference(userType.toUpperCase()).child(uid);

        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkEmailVerificationStatus();
            }
        });

        resendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resendVerificationEmail();
            }
        });

        // Initially check the email verification status
        checkEmailVerificationStatus();
    }

    private void checkEmailVerificationStatus() {
        if (currentUser != null) {
            currentUser.reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        if (currentUser.isEmailVerified()) {
                            Toast.makeText(EmailVerificationActivity.this, "Email verified successfully!", Toast.LENGTH_SHORT).show();
                            updateUserVerifiedStatus();
                            navigateToHomeActivity();
                        } else {
                            Toast.makeText(EmailVerificationActivity.this, "Please verify your email.", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(EmailVerificationActivity.this, "Failed to reload user.", Toast.LENGTH_SHORT).show();
                        Log.e("EmailVerification", "Reload failed: " + task.getException().getMessage());
                    }
                }
            });
        } else {
            Toast.makeText(EmailVerificationActivity.this, "No current user.", Toast.LENGTH_SHORT).show();
        }
    }

    private void resendVerificationEmail() {
        if (currentUser != null) {
            currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(EmailVerificationActivity.this, "Verification email sent.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(EmailVerificationActivity.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                        Log.e("EmailVerification", "Send email failed: " + task.getException().getMessage());
                    }
                }
            });
        }
    }

    private void updateUserVerifiedStatus() {
        if (currentUser != null) {
            reference.child("verified").setValue(true).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Log.d("EmailVerification", "User verification status updated successfully.");
                } else {
                    Log.e("EmailVerification", "Failed to update user verification status: " + task.getException().getMessage());
                    Toast.makeText(EmailVerificationActivity.this, "Failed to update verification status.", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Log.e("EmailVerification", "Current user is null.");
            Toast.makeText(EmailVerificationActivity.this, "No current user found.", Toast.LENGTH_SHORT).show();
        }
    }

    private void navigateToHomeActivity() {
        Class<?> targetActivity;

        switch (userType) {
            case "student":
                targetActivity = StudentHomeActivity.class;
                break;
            case "teacher":
                targetActivity = TeacherHomeActivity.class;
                break;
            case "principal":
                targetActivity = HeadActivity.class;
                break;
            default:
                targetActivity = MainActivity.class;
                break;
        }

        Intent intent = new Intent(EmailVerificationActivity.this, targetActivity);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}