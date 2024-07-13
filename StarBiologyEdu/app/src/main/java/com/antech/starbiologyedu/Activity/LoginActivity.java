package com.antech.starbiologyedu.Activity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private ProgressBar load;
    private CardView loginBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        mAuth = FirebaseAuth.getInstance();
        load = findViewById(R.id.loadLoginActivity);
        load.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.app_green),
                android.graphics.PorterDuff.Mode.SRC_IN);
        loginBtn = findViewById(R.id.loginBtn);

        ImageSlider imageSlider = findViewById(R.id.slider);
        ArrayList<SlideModel> slideModels = new ArrayList<>();
        slideModels.add(new SlideModel(R.drawable.slide1, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.slide2, ScaleTypes.CENTER_CROP));
        slideModels.add(new SlideModel(R.drawable.slide3, ScaleTypes.CENTER_CROP));
        imageSlider.setImageList(slideModels, ScaleTypes.CENTER_CROP);

        // Configure Google Sign-In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        // Check if the user is already signed in
        checkIfUserIsSignedIn();

        loginBtn.setOnClickListener(v -> signIn());

        setupPrivacyPolicyText();
    }

    private void checkIfUserIsSignedIn() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // User is signed in, navigate to HomeActivity
            navigateToMainActivity();
        }
    }

    private void signIn() {
        showProgressBar();
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                if (account != null) {
                    firebaseAuthWithGoogle(account.getIdToken());
                }
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign in failed.", Toast.LENGTH_SHORT).show();
                hideProgressBar();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    hideProgressBar();
                    if (task.isSuccessful()) {
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            // Check if user data exists and save if not
                            checkAndSaveUserData(user);
                        }
                    } else {
                        Toast.makeText(this, "Authentication Failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkAndSaveUserData(FirebaseUser user) {
        if (user != null) {
            String uid = user.getUid();
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(uid)
                    .get()
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful() && task.getResult().exists()) {
                            // User data exists, navigate to HomeActivity
                            navigateToMainActivity();
                        } else {
                            // User data does not exist, create new user
                            saveUserDataToFirestore(user);
                        }
                    });
        }
    }

    private void saveUserDataToFirestore(FirebaseUser user) {
        if (user != null) {
            String uid = user.getUid();
            String name = user.getDisplayName();
            String email = user.getEmail();
            String photoUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;

            User userData = new User(name, email, photoUrl);

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            db.collection("users").document(uid)
                    .set(userData)
                    .addOnSuccessListener(aVoid -> {
                        navigateToMainActivity();
                    })
                    .addOnFailureListener(e -> Log.w("LoginActivity", "Error saving user data", e));
        }
    }

    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void showProgressBar() {
        load.setVisibility(View.VISIBLE);
    }

    private void hideProgressBar() {
        load.setVisibility(View.GONE);
    }

    private void setupPrivacyPolicyText() {
        TextView ppText = findViewById(R.id.regLogPPText);
        String text = "By continuing, I agree to Privacy Policy";
        SpannableString spannableString = new SpannableString(text);

        // Get the custom color from resources
        int customColor = ContextCompat.getColor(this, R.color.app_yellow);

        // Set color and click listener for "Privacy Policy"
        int start1 = text.indexOf("Privacy Policy");
        int end1 = start1 + "Privacy Policy".length();
        ClickableSpan clickableSpan1 = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();
                DocumentReference docRef = firestore.collection("appInfo").document("privacyPolicy");

                // Fetch the privacy policy URL
                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        // Get the URL from the document
                        String privacyPolicyUrl = documentSnapshot.getString("url");

                        if (privacyPolicyUrl != null && !privacyPolicyUrl.isEmpty()) {
                            // Open the URL in a web browser
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(privacyPolicyUrl));
                            startActivity(intent);
                        } else {
                            Toast.makeText(LoginActivity.this, "Privacy policy URL not found.", Toast.LENGTH_SHORT).show();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Handle possible errors
                        Toast.makeText(LoginActivity.this, "Failed to load privacy policy.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void updateDrawState(@NonNull TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(customColor);
                ds.setUnderlineText(false);
            }
        };
        spannableString.setSpan(new ForegroundColorSpan(customColor), start1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(clickableSpan1, start1, end1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ppText.setText(spannableString);
        ppText.setMovementMethod(LinkMovementMethod.getInstance());
        ppText.setHighlightColor(Color.TRANSPARENT); // Prevent text highlight on click
    }

    public static class User {
        private String name;
        private String email;
        private String photoUrl;

        public User() {
            // Default constructor required for calls to DataSnapshot.getValue(User.class)
        }

        public User(String name, String email, String photoUrl) {
            this.name = name;
            this.email = email;
            this.photoUrl = photoUrl;
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

        public String getPhotoUrl() {
            return photoUrl;
        }

        public void setPhotoUrl(String photoUrl) {
            this.photoUrl = photoUrl;
        }
    }

}