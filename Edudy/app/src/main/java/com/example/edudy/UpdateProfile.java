package com.example.edudy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class UpdateProfile extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    private DatabaseReference userRef;
    private StorageReference mStore;
    private FirebaseAuth mAuth;
    private EditText updateUsername, updateMob;
    private TextView updateEmail;
    private CircleImageView updateImg;
    private FloatingActionButton chooseImgBtn;
    private Uri imageUri;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_profile);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Initialize UI elements
        updateUsername = findViewById(R.id.updateUsername);
        updateEmail = findViewById(R.id.updateEmail);
        updateMob = findViewById(R.id.updateMob);
        updateImg = findViewById(R.id.updateImg);
        chooseImgBtn = findViewById(R.id.chooseImg);

        // Initialize ProgressDialog
        progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Updating profile...");
        progressDialog.setCancelable(false);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        if (currentUser != null) {
            String userId = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference("EdudyUsers").child(userId);
            mStore = FirebaseStorage.getInstance().getReference().child("profileImagesUrl").child(userId + ".jpg");

            // Set up click listeners
            findViewById(R.id.saveProfileBtn).setOnClickListener(v -> updateProfileInfo());
            chooseImgBtn.setOnClickListener(v -> chooseImage());

            // Fetch user data and populate UI
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        String userName = snapshot.child("name").getValue(String.class);
                        String emailId = snapshot.child("email").getValue(String.class);
                        String mobNumber = snapshot.child("mob").getValue(String.class);
                        String profileImageUrl = snapshot.child("profileImageUrl").getValue(String.class);

                        updateUsername.setText(userName);
                        updateEmail.setText(emailId);
                        updateMob.setText(mobNumber);

                        if (profileImageUrl != null && !profileImageUrl.isEmpty()) {
                            Glide.with(UpdateProfile.this)
                                    .load(profileImageUrl)
                                    .placeholder(R.mipmap.ic_launcher)
                                    .error(R.mipmap.ic_launcher)
                                    .into(updateImg);
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(UpdateProfile.this, "Failed to load user data", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private void updateProfileInfo() {
        String username = updateUsername.getText().toString().trim();
        String email = updateEmail.getText().toString().trim();
        String mobile = updateMob.getText().toString().trim();
        String fullMobile = "+91" + mobile;


        if (TextUtils.isEmpty(username) || TextUtils.isEmpty(email) || TextUtils.isEmpty(mobile)) {
            Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
            return;
        }

        progressDialog.show(); // Show the ProgressDialog

        Map<String, Object> profileMap = new HashMap<>();
        profileMap.put("name", username);
        profileMap.put("email", email);
        profileMap.put("mob", fullMobile);

        userRef.updateChildren(profileMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    if (imageUri != null) {
                        uploadProfileImage();
                    } else {
                        progressDialog.dismiss(); // Dismiss the ProgressDialog
                        Toast.makeText(UpdateProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    progressDialog.dismiss(); // Dismiss the ProgressDialog
                    Toast.makeText(UpdateProfile.this, "Profile update failed", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
                updateImg.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadProfileImage() {
        if (imageUri != null) {
            mStore.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    mStore.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            userRef.child("profileImageUrl").setValue(uri.toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            progressDialog.dismiss(); // Dismiss the ProgressDialog
                                            if (task.isSuccessful()) {
                                                Toast.makeText(UpdateProfile.this, "Profile updated successfully", Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(UpdateProfile.this, "Failed to update profile", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressDialog.dismiss(); // Dismiss the ProgressDialog
                            Toast.makeText(UpdateProfile.this, "Profile image upload failed", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    progressDialog.dismiss(); // Dismiss the ProgressDialog
                    Toast.makeText(UpdateProfile.this, "Profile image upload failed", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}