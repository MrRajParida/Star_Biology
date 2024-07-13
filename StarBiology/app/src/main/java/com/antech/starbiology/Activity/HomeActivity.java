package com.antech.starbiology.Activity;


import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.antech.starbiology.Fragment.HomeFragment;
import com.antech.starbiology.Fragment.NotesFragment;
import com.antech.starbiology.Fragment.ProfileFragment;
import com.antech.starbiology.Fragment.VideosFragment;
import com.antech.starbiology.Fragment.QuizFragment;
import com.antech.starbiology.R;

public class HomeActivity extends AppCompatActivity {

    private static final int REQUEST_NOTIFICATION_PERMISSION = 1;
    private boolean doubleBackToExitPressedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        allowNotification();
        appInUpdate();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
        }

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();
            if (id == R.id.nav_home) {
                selectedFragment = new HomeFragment();
            } else if (id == R.id.nav_notes) {
                selectedFragment = new NotesFragment();
            } else if (id == R.id.nav_videos) {
                selectedFragment = new VideosFragment();
            } else if (id == R.id.nav_quiz) {
                selectedFragment = new QuizFragment();
            } else if (id == R.id.nav_account) {
                selectedFragment = new ProfileFragment();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
            return true;
        });
    }

    private void allowNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, REQUEST_NOTIFICATION_PERMISSION);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_NOTIFICATION_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "You can receive all messages from Star Biology", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Sorry, you can not receive notification from Star Biology", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void appInUpdate() {
        AppUpdateManager appUpdateManager = AppUpdateManagerFactory.create(this);
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE) {
                android.view.ContextThemeWrapper ctw = new android.view.ContextThemeWrapper(this, R.style.Theme_StarBiology);
                final android.app.AlertDialog.Builder alertDialogBuilder = new android.app.AlertDialog.Builder(ctw);

                alertDialogBuilder.setTitle("Update Star Biology");
                alertDialogBuilder.setCancelable(false);
                alertDialogBuilder.setIcon(R.drawable.baseline_notifications_none_24);
                alertDialogBuilder.setMessage("Hey there! We wanted to let you know that we've just released a new update for our app. This update includes some great new features and improvements, so we highly recommend you update to the latest version as soon as possible. Thanks, and we hope you enjoy the new update!");


                String packageName = getPackageName();
                String playStoreUrl = "https://play.google.com/store/apps/details?id=" + packageName;


                alertDialogBuilder.setPositiveButton("Update \uD83D\uDE80", (dialog, id) -> {
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUrl)));
                    } catch (ActivityNotFoundException e) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUrl)));
                    }
                });


                alertDialogBuilder.setNegativeButton("No Thanks \uD83D\uDEAB", (dialog, id) -> finish());

                alertDialogBuilder.show();
            }
        });
    }


    @Override
    public void onBackPressed() {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        if (!(currentFragment instanceof HomeFragment)) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            bottomNavigationView.setSelectedItemId(R.id.nav_home);
        } else {
            if (doubleBackToExitPressedOnce) {
                super.onBackPressed();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Press back again to exit", Toast.LENGTH_SHORT).show();
            new Handler(Looper.getMainLooper()).postDelayed(() -> doubleBackToExitPressedOnce = false, 2000);
        }
    }
}

