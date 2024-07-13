package com.example.logregpanel;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

public class PrincipalRegister extends AppCompatActivity {
    private TextInputEditText userId, mailId, password;
    private TextInputLayout userNameLayout, emailLayout, passwordLayout;
    private DatePicker datePicker;
    private Button regBtn;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private CheckBox termConditionCheckBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_principal_register);

        userId = findViewById(R.id.headRegUserId);
        mailId = findViewById(R.id.headRegEmail);
        password = findViewById(R.id.headRegPassword);
        datePicker = findViewById(R.id.regDatePicker);
        userNameLayout = findViewById(R.id.headRegUserNameInput);
        emailLayout = findViewById(R.id.headRegEmailInput);
        passwordLayout = findViewById(R.id.headRegPasswordInput);
        regBtn = findViewById(R.id.headRegBtn);
        termConditionCheckBox = findViewById(R.id.regCheckBox);
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Registering...");
        mAuth = FirebaseAuth.getInstance();

        regBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (termConditionCheckBox.isChecked()) {
                    confirmRegister();
                } else {
                    Toast.makeText(PrincipalRegister.this, "Please accept the terms and conditions", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void confirmRegister() {
        new AlertDialog.Builder(this)
                .setTitle("Are you sure you want to register?")
                .setMessage("After clicking OK you can register with us...")
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setComponentsEnabled(false);
                        registerThisUser();
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setComponentsEnabled(true);
                        Toast.makeText(PrincipalRegister.this, "Enter details carefully before registering", Toast.LENGTH_SHORT).show();
                    }
                })
                .show();
    }

    private void registerThisUser() {
        String userName = userId.getText().toString().trim();
        String userEmail = mailId.getText().toString().trim();
        String userPass = password.getText().toString().trim();

        int day = datePicker.getDayOfMonth();
        int month = datePicker.getMonth();
        int year = datePicker.getYear();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String dobString = dateFormat.format(calendar.getTime());

        if (userName.isEmpty()) {
            userNameLayout.setError("Please enter your name");
            userId.requestFocus();
        } else if (userEmail.isEmpty()) {
            emailLayout.setError("Please enter your email");
            mailId.requestFocus();
        } else if (!isValidEmail(userEmail)) {
            emailLayout.setError("Please enter a valid email");
            mailId.requestFocus();
        } else if (!isValidPassword(userPass)) {
            passwordLayout.setError("Password must be 8-13 characters long and contain both letters and numbers");
            password.requestFocus();
        } else {
            userNameLayout.setError(null);
            emailLayout.setError(null);
            passwordLayout.setError(null);
            checkIfNameExistsAndRegister(userName, userEmail, userPass, dobString);
        }
    }

    private boolean isValidEmail(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    private boolean isValidPassword(String password) {
        if (password.length() < 8 || password.length() > 13) {
            return false;
        }
        boolean hasLetter = false;
        boolean hasDigit = false;
        for (char c : password.toCharArray()) {
            if (Character.isLetter(c)) {
                hasLetter = true;
            } else if (Character.isDigit(c)) {
                hasDigit = true;
            }
            if (hasLetter && hasDigit) {
                return true;
            }
        }
        return false;
    }

    private void checkIfNameExistsAndRegister(String userName, String userEmail, String userPass, String dob) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("PRINCIPAL");
        reference.orderByChild("name").equalTo(userName).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    Toast.makeText(PrincipalRegister.this, "This name is already registered", Toast.LENGTH_SHORT).show();
                    setComponentsEnabled(true);
                } else {
                    performRegistration(userName, userEmail, userPass, dob);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(PrincipalRegister.this, "Database Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                setComponentsEnabled(true);
            }
        });
    }

    private void performRegistration(String userName, String userEmail, String userPass, String dob) {
        progressDialog.show();
        mAuth.createUserWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    FirebaseUser user = mAuth.getCurrentUser();
                    if (user != null) {
                        user.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    String uid = user.getUid();
                                    HashMap<String, Object> hashMap = new HashMap<>();
                                    hashMap.put("email", userEmail);
                                    hashMap.put("uid", uid);
                                    hashMap.put("name", userName);
                                    hashMap.put("profileImageUrl", "");
                                    hashMap.put("mob", "");
                                    hashMap.put("dob", dob);
                                    hashMap.put("verified", false);

                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference reference = database.getReference("PRINCIPAL");
                                    reference.child(uid).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                progressDialog.dismiss();
                                                Toast.makeText(PrincipalRegister.this, "Registration successful. Please verify your email.", Toast.LENGTH_LONG).show();
                                                navigateToEmailVerificationActivity(userEmail, userName, uid);
                                            } else {
                                                progressDialog.dismiss();
                                                Toast.makeText(PrincipalRegister.this, "Database Error", Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });
                                } else {
                                    progressDialog.dismiss();
                                    Toast.makeText(PrincipalRegister.this, "Failed to send verification email.", Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                } else {
                    progressDialog.dismiss();
                    Toast.makeText(PrincipalRegister.this, "Registration Failed", Toast.LENGTH_LONG).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Toast.makeText(PrincipalRegister.this, "Error Occurred: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }

    private void navigateToEmailVerificationActivity(String userEmail, String userName, String uid) {
        Intent mainIntent = new Intent(PrincipalRegister.this, EmailVerificationActivity.class);
        mainIntent.putExtra("userType", "principal");
        mainIntent.putExtra("email", userEmail);
        mainIntent.putExtra("username", userName);  // Correct key for the username
        mainIntent.putExtra("uid", uid);  // Adding uid to intent
        startActivity(mainIntent);
        finish();
    }

    private void setComponentsEnabled(boolean enabled) {
        userId.setEnabled(enabled);
        mailId.setEnabled(enabled);
        password.setEnabled(enabled);
        datePicker.setEnabled(enabled);
        regBtn.setEnabled(enabled);
    }
}