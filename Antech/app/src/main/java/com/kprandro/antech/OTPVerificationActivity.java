package com.kprandro.antech;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.view.KeyEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.concurrent.TimeUnit;

public class OTPVerificationActivity extends AppCompatActivity {
    private EditText inputNumber1, inputNumber2, inputNumber3, inputNumber4, inputNumber5, inputNumber6;
    private TextView resendOtp;
    private String getOtpBackend;
    private ProgressBar loading;
    private FloatingActionButton submitBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        inputNumber1 = findViewById(R.id.inputotp1);
        inputNumber2 = findViewById(R.id.inputotp2);
        inputNumber3 = findViewById(R.id.inputotp3);
        inputNumber4 = findViewById(R.id.inputotp4);
        inputNumber5 = findViewById(R.id.inputotp5);
        inputNumber6 = findViewById(R.id.inputotp6);
        submitBtn = findViewById(R.id.submitBtn);
        resendOtp = findViewById(R.id.resendOtp);
        loading = findViewById(R.id.OTPVerificationLoading);

        // Setting color of the loading spinner
        loading.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.appText),
                android.graphics.PorterDuff.Mode.SRC_IN);

        // Setup number movement and backspace handling for OTP fields
        numberOtpMove();

        // Set the text for verification number
        TextView textView = findViewById(R.id.showEnteredNum);
        textView.setText(String.format("Verify your entered number as +91 %s", getIntent().getStringExtra("mobile")));
        getOtpBackend = getIntent().getStringExtra("OTP");

        // Set up verify button click listener
        submitBtn.setOnClickListener(view -> {
            if (isAllOtpFieldsFilled()) {
                String enterCodeOtp = getEnteredOtp();
                if (getOtpBackend != null) {
                    loading.setVisibility(View.VISIBLE);
                    submitBtn.setVisibility(View.GONE);
                    verifyOtp(enterCodeOtp);
                } else {
                    Toast.makeText(OTPVerificationActivity.this, "Please enter all Numbers", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(OTPVerificationActivity.this, "Please enter all Numbers", Toast.LENGTH_SHORT).show();
            }
        });

        // Set up resend OTP clickable span
        setupResendOtpTextView();

        // Start OTP verification process
        startOtpVerification();
    }

    private boolean isAllOtpFieldsFilled() {
        return !inputNumber1.getText().toString().trim().isEmpty() &&
                !inputNumber2.getText().toString().trim().isEmpty() &&
                !inputNumber3.getText().toString().trim().isEmpty() &&
                !inputNumber4.getText().toString().trim().isEmpty() &&
                !inputNumber5.getText().toString().trim().isEmpty() &&
                !inputNumber6.getText().toString().trim().isEmpty();
    }

    private String getEnteredOtp() {
        return inputNumber1.getText().toString() +
                inputNumber2.getText().toString() +
                inputNumber3.getText().toString() +
                inputNumber4.getText().toString() +
                inputNumber5.getText().toString() +
                inputNumber6.getText().toString();
    }

    private void verifyOtp(String enteredOtp) {
        PhoneAuthCredential phoneAuthCredential = PhoneAuthProvider.getCredential(getOtpBackend, enteredOtp);
        FirebaseAuth.getInstance().signInWithCredential(phoneAuthCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                loading.setVisibility(View.GONE);
                submitBtn.setVisibility(View.VISIBLE);

                if (task.isSuccessful()) {
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                } else {
                    Toast.makeText(OTPVerificationActivity.this, "Enter the correct OTP", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void setupResendOtpTextView() {
        String text = "Didn't receive OTP? Resend again!";
        SpannableString spannableString = new SpannableString(text);

        int start1 = text.indexOf("Resend again!");
        int end1 = start1 + "Resend again!".length();
        ForegroundColorSpan colorSpan1 = new ForegroundColorSpan(Color.BLUE);
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                Toast.makeText(OTPVerificationActivity.this, "Sending...", Toast.LENGTH_SHORT).show();
                resendOtp();
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.rgb(255, 111, 97)); // Text color
                ds.setUnderlineText(false); // Remove underline
            }
        };
        spannableString.setSpan(colorSpan1, start1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpan1, start1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        resendOtp.setText(spannableString);
        resendOtp.setMovementMethod(LinkMovementMethod.getInstance());
        resendOtp.setHighlightColor(Color.TRANSPARENT);
    }

    private void startOtpVerification() {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + getIntent().getStringExtra("mobile"),
                60,
                TimeUnit.SECONDS,
                OTPVerificationActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        // Optional: handle auto-verification or auto-retrieval
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        Toast.makeText(OTPVerificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String newBackendOtp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        getOtpBackend = newBackendOtp;
                        Toast.makeText(OTPVerificationActivity.this, "OTP sent Successfully", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void resendOtp() {
        loading.setVisibility(View.VISIBLE);
        resendOtp.setVisibility(View.INVISIBLE);

        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                "+91" + getIntent().getStringExtra("mobile"),
                60,
                TimeUnit.SECONDS,
                OTPVerificationActivity.this,
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                        // Optional: handle auto-verification or auto-retrieval
                    }

                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        loading.setVisibility(View.GONE);
                        resendOtp.setVisibility(View.VISIBLE);
                        Toast.makeText(OTPVerificationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCodeSent(@NonNull String newBackendOtp, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                        loading.setVisibility(View.GONE);
                        resendOtp.setVisibility(View.VISIBLE);
                        getOtpBackend = newBackendOtp;
                        Toast.makeText(OTPVerificationActivity.this, "OTP resent Successfully", Toast.LENGTH_SHORT).show();
                    }
                }
        );
    }

    private void numberOtpMove() {
        inputNumber1.addTextChangedListener(createTextWatcher(inputNumber1, inputNumber2));
        inputNumber2.addTextChangedListener(createTextWatcher(inputNumber2, inputNumber3));
        inputNumber3.addTextChangedListener(createTextWatcher(inputNumber3, inputNumber4));
        inputNumber4.addTextChangedListener(createTextWatcher(inputNumber4, inputNumber5));
        inputNumber5.addTextChangedListener(createTextWatcher(inputNumber5, inputNumber6));
        inputNumber6.addTextChangedListener(createTextWatcher(inputNumber6, null));

        setupBackspaceListener(inputNumber1, null);
        setupBackspaceListener(inputNumber2, inputNumber1);
        setupBackspaceListener(inputNumber3, inputNumber2);
        setupBackspaceListener(inputNumber4, inputNumber3);
        setupBackspaceListener(inputNumber5, inputNumber4);
        setupBackspaceListener(inputNumber6, inputNumber5);
    }

    private TextWatcher createTextWatcher(final EditText currentEditText, final EditText nextEditText) {
        return new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // No action needed
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().trim().isEmpty() && nextEditText != null) {
                    nextEditText.requestFocus();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                // No action needed
            }
        };
    }

    private void setupBackspaceListener(final EditText currentEditText, final EditText previousEditText) {
        currentEditText.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_DEL && event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (currentEditText.getText().toString().isEmpty() && previousEditText != null) {
                        previousEditText.requestFocus();
                        previousEditText.setText("");
                    }
                }
                return false;
            }
        });
    }
}
