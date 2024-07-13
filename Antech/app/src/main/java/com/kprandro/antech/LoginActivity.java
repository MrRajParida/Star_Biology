package com.kprandro.antech;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends AppCompatActivity {
    private TextInputEditText mobileEditText;
    private TextInputLayout mobileLayout;
    private FloatingActionButton registerBtn;
    private ProgressBar loading;
    private FirebaseFirestore db;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        FirebaseApp.initializeApp(this);
        db = FirebaseFirestore.getInstance();

        loading = findViewById(R.id.loginLoading);
        loading.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.appText),
                android.graphics.PorterDuff.Mode.SRC_IN);
        registerBtn = findViewById(R.id.regBtn);
        registerBtn.setVisibility(View.GONE);
        registerBtn.setBackgroundColor(Color.GRAY);
        mobileEditText = findViewById(R.id.mobileEditText);
        mobileLayout = findViewById(R.id.mobileNumberEnterInputLayout);

        mobileEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                registerBtn.setEnabled(charSequence.length() == 10);
                if (charSequence.length() == 10) {
                    registerBtn.setVisibility(View.VISIBLE);
                } else {
                    registerBtn.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String mobileNumber = mobileEditText.getText().toString().trim();
                if (!mobileNumber.isEmpty() && mobileNumber.length() == 10) {
                    loading.setVisibility(View.VISIBLE);
                    PhoneAuthProvider.getInstance().verifyPhoneNumber(
                            "+91" + mobileNumber,
                            60,
                            TimeUnit.SECONDS,
                            LoginActivity.this,
                            new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                                @Override
                                public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                    loading.setVisibility(View.GONE);
                                }

                                @Override
                                public void onVerificationFailed(@NonNull FirebaseException e) {
                                    loading.setVisibility(View.GONE);
                                    Toast.makeText(LoginActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onCodeSent(@NonNull String backendotp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                    loading.setVisibility(View.GONE);
                                    saveMobileNumberToFireStore(mobileNumber);
                                    Intent intent = new Intent(LoginActivity.this, OTPVerificationActivity.class);
                                    intent.putExtra("mobile", mobileNumber);
                                    intent.putExtra("OTP", backendotp); // Corrected key to match OTPVerification.java
                                    startActivity(intent);
                                }
                            }
                    );
                } else {
                    Toast.makeText(LoginActivity.this, "Please enter a valid 10-digit mobile number", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setupPrivacyPolicyText();
    }

    private void saveMobileNumberToFireStore(String mobileNumber) {
        Map<String, Object> user = new HashMap<>();
        user.put("mobile", mobileNumber);

        db.collection("users")
                .add(user)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(LoginActivity.this, "Mobile number saved successfully", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(LoginActivity.this, "Error saving mobile number: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void setupPrivacyPolicyText() {
        TextView ppText = findViewById(R.id.regLogPPText);
        String text = "By continuing, I agree to Privacy Policy";
        SpannableString spannableString = new SpannableString(text);

        int start1 = text.indexOf("Privacy Policy");
        int end1 = start1 + "Privacy Policy".length();
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://loremipsum.io/privacy-policy/"));
                startActivity(browserIntent);
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(getColor(R.color.appText));
                ds.setUnderlineText(false);
            }
        };
        spannableString.setSpan(new ForegroundColorSpan(getColor(R.color.appText)), start1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpan1, start1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ppText.setText(spannableString);
        ppText.setMovementMethod(LinkMovementMethod.getInstance());
        ppText.setHighlightColor(Color.TRANSPARENT);
    }
}