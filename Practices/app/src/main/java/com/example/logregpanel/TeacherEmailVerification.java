package com.example.logregpanel;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TeacherEmailVerification extends AppCompatActivity {
    private TextView verificationMessage;
    private Button resendVerificationEmailBtn;
    private Button checkVerificationStatusBtn;
    private FirebaseAuth mAuth;
    private DatabaseReference userRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_teacher_enail_verification);

        mAuth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference("USER");

        verificationMessage = findViewById(R.id.verificationMessage);
        resendVerificationEmailBtn = findViewById(R.id.resendVerificationEmailBtn);
        checkVerificationStatusBtn = findViewById(R.id.checkVerificationStatusBtn);

        resendVerificationEmailBtn.setOnClickListener(v -> resendVerificationEmail());
        checkVerificationStatusBtn.setOnClickListener(v -> checkVerificationStatus());
    }

    private void resendVerificationEmail() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            user.sendEmailVerification()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(TeacherEmailVerification.this, "Verification email sent.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(TeacherEmailVerification.this, "Failed to send verification email.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void checkVerificationStatus() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            if (user.isEmailVerified()) {
                // Update the user's verified status in the database
                updateUserVerifiedStatus();
                // Redirect to MainActivity
                navigateToMainActivity();
            } else {
                Toast.makeText(TeacherEmailVerification.this, "Email is not verified yet. Please check your inbox.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void updateUserVerifiedStatus() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            userRef.child(currentUser.getUid()).child("verified").setValue(true)
                    .addOnCompleteListener(task -> {
                        if (!task.isSuccessful()) {
                            Toast.makeText(TeacherEmailVerification.this, "Failed to update verification status.", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void navigateToMainActivity() {
        Intent mainIntent = new Intent(TeacherEmailVerification.this, MainActivity.class);
        mainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(mainIntent);
        finish();
    }
}
