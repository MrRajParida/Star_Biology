package com.antech.musicplayer;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        setupToolbar();


        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MyMusicFragment())
                    .commit();
        }


        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();
            if (id == R.id.nav_myMusic) {
                selectedFragment = new MyMusicFragment();
            } else if (id == R.id.nav_watch) {
                selectedFragment = new WatchFragment();
            }
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, selectedFragment)
                    .commit();
            return true;
        });
    }

    // Set up the toolbar and navigation drawer
    private void setupToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);  // Set toolbar as the ActionBar
        DrawerLayout drawerLayout = findViewById(R.id.drawerLayout);

        // Set up the Drawer Toggle with the custom toolbar
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar,
                R.string.open_nav, R.string.close_nav);

        toggle.setDrawerIndicatorEnabled(false);  // Disable the default hamburger icon

        // Use a custom icon for the navigation drawer
        @SuppressLint("UseCompatLoadingForDrawables")
        Drawable customIcon = getDrawable(R.drawable.baseline_align_horizontal_left_24);
        toolbar.setNavigationIcon(customIcon);

        // Set the toolbar navigation icon's click listener
        toolbar.setNavigationOnClickListener(view -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        // Sync the drawer toggle state
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }
}
