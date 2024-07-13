package com.example.edudy;

import static com.google.common.reflect.Reflection.getPackageName;

import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import java.util.List;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.smarteist.autoimageslider.SliderView;

import java.util.ArrayList;



public class HomeFragment extends Fragment {

    private SliderView sliderView;
    private ImageSliderAdapter adapter;
    private List<ImageModel> imageList;
    private ProgressBar progressBar;
    private DatabaseReference userRef;
    private StorageReference mStore;
    private FirebaseAuth mAuth;
    private TextView userNameIdHome;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private List<VideoBookmarkViewModelClass> bookmarkList;
    private VideoBookmarkAdapter bookmarkAdapter;
    private ProgressBar bookmarkProgressBar;
    private ImageButton ig, fb, yt, in;
    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        mAuth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String userId = currentUser.getUid();
            userRef = FirebaseDatabase.getInstance().getReference("EdudyUsers").child(userId);
            mStore = FirebaseStorage.getInstance().getReference();

            // Initialize other views
            NavigationView navigationView = view.findViewById(R.id.navigation_view);
            View headerView = navigationView.getHeaderView(0);
            ig = headerView.findViewById(R.id.btnIg);
            fb = headerView.findViewById(R.id.btnFb);
            yt = headerView.findViewById(R.id.btnYt);
            in = headerView.findViewById(R.id.btnIn);
            TextView navHeaderUserName = headerView.findViewById(R.id.getUserName);
            userNameIdHome = view.findViewById(R.id.userNameOnHome);

            // Read user data from Firebase Realtime Database
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.exists()) {
                        String userName = dataSnapshot.child("name").getValue(String.class);
                        navHeaderUserName.setText(userName);
                        userNameIdHome.setText(userName); // Set username on home fragment
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                }
            });
        }


        DrawerLayout drawerLayout = view.findViewById(R.id.drawer_layout);
        NavigationView navigationView = view.findViewById(R.id.navigation_view);
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                getActivity(), drawerLayout, toolbar,
                R.string.open_nav, R.string.close_nav);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(item -> {
            item.setChecked(true);
            drawerLayout.closeDrawer(GravityCompat.START);
            int id = item.getItemId();
            if (id == R.id.nav_share) {
                shareApp();
            } else if (id == R.id.nav_aboutUs) {
                aboutUs();
            } else if (id == R.id.nav_faqS) {
                startActivity(new Intent(getActivity(), FAQsActivity.class));
            } else if (id == R.id.nav_contactUs) {
                contactUs();
            } else if (id == R.id.nav_rateUs) {
                rateApp();
            } else if (id == R.id.nav_privacyPolicy) {
                Toast.makeText(getActivity(), "Privacy Policy", Toast.LENGTH_SHORT).show();
            } else if (id == R.id.nav_chatWithUs) {
                chatWith();
            }
            return true;
        });
        ig.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the context from the Fragment
                Context context = requireContext();

                // Use the context to get the PackageManager and launch Instagram
                Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.instagram.android");
                if (intent != null) {
                    context.startActivity(intent);
                } else {
                    // Instagram is not installed, open in a web browser
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.instagram.com/rajparida9_?igsh=ODB1Y2d1dWNlMDR5"));
                    context.startActivity(intent);
                }
            }
        });

        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the context from the Fragment
                Context context = requireContext();

                // Use the context to get the PackageManager and launch Instagram
                Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.facebook.android");
                if (intent != null) {
                    context.startActivity(intent);
                } else {
                    // Instagram is not installed, open in a web browser
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com"));
                    context.startActivity(intent);
                }
            }
        });

        yt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the context from the Fragment
                Context context = requireContext();

                // Use the context to get the PackageManager and launch Instagram
                Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.youtube.android");
                if (intent != null) {
                    context.startActivity(intent);
                } else {
                    // Instagram is not installed, open in a web browser
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/@codekpr"));
                    context.startActivity(intent);
                }
            }
        });

        in.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Get the context from the Fragment
                Context context = requireContext();

                // Use the context to get the PackageManager and launch Instagram
                Intent intent = context.getPackageManager().getLaunchIntentForPackage("com.linkedin.android");
                if (intent != null) {
                    context.startActivity(intent);
                } else {
                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.linkedin.com/in/raj-parida-ba10a6265/"));
                    context.startActivity(intent);
                }
            }
        });

        progressBar = view.findViewById(R.id.loadSlidingImages);
        progressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(getActivity(), R.color.app_color),
                android.graphics.PorterDuff.Mode.SRC_IN);
        sliderView = view.findViewById(R.id.imageSlider);
        imageList = new ArrayList<>();
        adapter = new ImageSliderAdapter(getContext(), imageList, progressBar);
        sliderView.setSliderAdapter(adapter);

        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);
        swipeRefreshLayout.setOnRefreshListener(this::refreshContent);

        recyclerView = view.findViewById(R.id.videoBookmarkRCV);
        bookmarkList = new ArrayList<>();
        bookmarkAdapter = new VideoBookmarkAdapter(getContext(), bookmarkList, progressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(bookmarkAdapter);

        fetchBookmarks();
        fetchImagesFromFirebase();

        return view;
    }
    private void aboutUs() {
        startActivity(new Intent(getActivity(), AboutUsActivity.class));
    }

    private void refreshContent() {
        // Perform data reload operations
        fetchImagesFromFirebase();
        fetchBookmarks();
    }

    private void chatWith() {
        String phoneNumber = "+916371801747";
        try {
            String url = "https://api.whatsapp.com/send?phone=" + phoneNumber;
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getActivity(), "WhatsApp not installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void contactUs() {
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("mailto:support@example.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback or Support");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear Support Team,\n\n");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send Email"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "No email app installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void rateApp() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getActivity().getPackageName())));
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getActivity().getPackageName())));
        }
    }

    private void shareApp() {
        String packageName = getActivity().getApplicationContext().getPackageName();
        String appLink = "https://play.google.com/store/apps/details?id=" + packageName;

        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this amazing app: " + appLink);
        shareIntent.setType("text/plain");

        if (shareIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        }
    }

    private void fetchImagesFromFirebase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("SLIDINGIMAGES");
        progressBar.setVisibility(View.VISIBLE); // Show progress bar

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                imageList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    ImageModel imageModel = dataSnapshot.getValue(ImageModel.class);
                    imageList.add(imageModel);
                }
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE); // Hide progress bar
                swipeRefreshLayout.setRefreshing(false); // Hide SwipeRefreshLayout's refresh animation
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE); // Hide progress bar on error
                swipeRefreshLayout.setRefreshing(false); // Hide SwipeRefreshLayout's refresh animation
            }
        });
    }

    private void fetchBookmarks() {

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference bookmarkRef = FirebaseDatabase.getInstance().getReference("EdudyUsers").child(userId).child("VideoBookmarks");

        bookmarkRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookmarkList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    VideoBookmarkViewModelClass bookmark = dataSnapshot.getValue(VideoBookmarkViewModelClass.class);
                    if (bookmark != null) {
                        bookmarkList.add(bookmark);
                    }
                }
                bookmarkAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Failed to load bookmarks: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}