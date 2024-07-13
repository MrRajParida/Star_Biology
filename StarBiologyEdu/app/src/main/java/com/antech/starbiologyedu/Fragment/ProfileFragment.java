package com.antech.starbiologyedu.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.starproduction.starbiology.Activity.LoginActivity;
import com.starproduction.starbiology.Adapter.BookmarkPagerAdapter;
import com.starproduction.starbiology.R;

import java.io.IOException;

import de.hdodenhof.circleimageview.CircleImageView;



public class ProfileFragment extends Fragment {
    private ProgressBar progressBar;
    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistration;
    private StorageReference mStore;
    private FirebaseAuth mAuth;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    private static final int PICK_IMAGE_REQUEST = 2;
    private CircleImageView profile;
    private Uri imageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        profile = view.findViewById(R.id.AccountProfile);
        TextView username = view.findViewById(R.id.AccountUsername);
        TextView email = view.findViewById(R.id.AccountEmailName);
        progressBar = view.findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(getActivity(), R.color.app_green),
                android.graphics.PorterDuff.Mode.SRC_IN);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProfileDialog();
            }
        });

        tabLayout = view.findViewById(R.id.tab_layout);
        viewPager = view.findViewById(R.id.view_pager);
        BookmarkPagerAdapter viewPagerAdapter = new BookmarkPagerAdapter(getActivity());
        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                tabLayout.getTabAt(position).select();
            }
        });

        if (currentUser != null && isAdded()) {
            progressBar.setVisibility(View.VISIBLE);
            String userId = currentUser.getUid();
            db = FirebaseFirestore.getInstance();
            mStore = FirebaseStorage.getInstance().getReference();

            listenerRegistration = db.collection("users")
                    .document(userId)
                    .addSnapshotListener((documentSnapshot, e) -> {
                        if (e != null) {
                            // Handle the error
                            progressBar.setVisibility(View.GONE);
                            Toast.makeText(getActivity(), "Error loading profile", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        if (documentSnapshot != null && documentSnapshot.exists() && isAdded() && getView() != null) {
                            String userName = documentSnapshot.getString("name");
                            username.setText(userName);
                            String emailId = documentSnapshot.getString("email");
                            email.setText(emailId);
                            String profileImageUrl = documentSnapshot.getString("photoUrl");
                            Glide.with(requireContext())
                                    .load(profileImageUrl)
                                    .placeholder(R.drawable.starbiologyprofiledefaultimg)
                                    .error(R.drawable.starbiologyprofiledefaultimg)
                                    .into(profile);
                        }
                        progressBar.setVisibility(View.GONE);
                    });
        }

        setHasOptionsMenu(true);
        return view;
    }

    private void showProfileDialog() {
            // Inflate the dialog layout
            LayoutInflater inflater = LayoutInflater.from(requireContext());
            View dialogView = inflater.inflate(R.layout.dialog_image_view, null);
            ImageView largeImageView = dialogView.findViewById(R.id.largeImageView);

            // Get the user's document and handle the result asynchronously
            db.collection("users")
                    .document(mAuth.getCurrentUser().getUid())
                    .get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            String profileImageUrl = snapshot.getString("photoUrl");
                            if (profileImageUrl != null) {
                                Glide.with(this)
                                        .load(profileImageUrl)
                                        .placeholder(R.drawable.starbiologyprofiledefaultimg)
                                        .error(R.drawable.starbiologyprofiledefaultimg)
                                        .into(largeImageView);
                            } else {
                                Toast.makeText(requireContext(), "No image found", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Error loading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });

            // Create and show the dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(requireContext()); // Use a custom theme if needed
            builder.setView(dialogView);

            AlertDialog dialog = builder.create();
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent); // Make dialog background transparent
            dialog.show();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.account_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.edit_account) {
            updateProfile();
            return true;
        } else if (id == R.id.edit_logout) {
            AppLogout();
            return true;
        } else if (id == R.id.edit_notification) {
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
            if (!notificationManager.areNotificationsEnabled()) {
                showAllowNotificationDialog();
            } else {
                Toast.makeText(getActivity(), "Notifications are already enabled", Toast.LENGTH_SHORT).show();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void updateProfile() {
        if (!isAdded()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Update Profile Image");
        builder.setMessage("Are you sure you want to Change Image");

        builder.setPositiveButton("Update", (dialog, which) -> {
            chooseImage(); // Call method to choose image
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void chooseImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Profile Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireContext().getContentResolver(), imageUri);
                profile.setImageBitmap(bitmap);
                uploadProfileImage(); // Call method to upload image
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void uploadProfileImage() {
        if (imageUri != null) {
            progressBar.setVisibility(View.VISIBLE); // Show progress bar

            String userId = mAuth.getCurrentUser().getUid();
            StorageReference imageRef = mStore.child("profileImages").child(userId + ".jpg");

            imageRef.putFile(imageUri)
                    .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            imageRef.getDownloadUrl()
                                    .addOnSuccessListener(new OnSuccessListener<Uri>() {
                                        @Override
                                        public void onSuccess(Uri uri) {
                                            updateProfileImageUrl(uri.toString()); // Update Firestore with new image URL
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            progressBar.setVisibility(View.GONE); // Hide progress bar
                                            Toast.makeText(getActivity(), "Failed to get image URL", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            progressBar.setVisibility(View.GONE); // Hide progress bar
                            Toast.makeText(getActivity(), "Failed to upload image", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void updateProfileImageUrl(String imageUrl) {
        String userId = mAuth.getCurrentUser().getUid();
        DocumentReference userRef = db.collection("users").document(userId);

        userRef.update("photoUrl", imageUrl)
                .addOnSuccessListener(aVoid -> {
                    progressBar.setVisibility(View.GONE); // Hide progress bar
                    Toast.makeText(getActivity(), "Profile image updated", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    progressBar.setVisibility(View.GONE); // Hide progress bar
                    Toast.makeText(getActivity(), "Failed to update profile image", Toast.LENGTH_SHORT).show();
                });
    }

    private void showAllowNotificationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Enable Notifications");
        builder.setMessage("Notifications are currently disabled. Would you like to enable them?");
        builder.setPositiveButton("Yes", (dialog, which) -> allowNotification());
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
        builder.create().show();
    }

    private void allowNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
            } else {
                openNotificationSettings();
            }
        } else {
            openNotificationSettings();
        }
    }

    private void openNotificationSettings() {
        Intent intent = new Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS)
                .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .putExtra(Settings.EXTRA_APP_PACKAGE, getActivity().getPackageName());
        startActivity(intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getActivity(), "Notifications enabled", Toast.LENGTH_SHORT).show();
                openNotificationSettings();
            } else {
                Toast.makeText(getActivity(), "Permission denied, cannot enable notifications", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void AppLogout() {
        if (!isAdded()) {
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Logout");
        builder.setMessage("Are you sure you want to logout?");

        builder.setPositiveButton("Logout", (dialog, which) -> {
            mAuth.signOut();
            GoogleSignIn.getClient(requireContext(), GoogleSignInOptions.DEFAULT_SIGN_IN).signOut()
                    .addOnCompleteListener(requireActivity(), task -> {
                        Toast.makeText(requireContext(), "Successfully logged out", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivity(intent);
                        requireActivity().finish(); // Close the current activity
                    });
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
}