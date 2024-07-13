package com.antech.starbiologyedu.Fragment;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.starproduction.starbiology.Adapter.QuestionAdapter;
import com.starproduction.starbiology.ModelClass.QuestionModelClass;
import com.starproduction.starbiology.Question.QuestionActivity;
import com.starproduction.starbiology.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


public class HomeFragment extends Fragment {

    private QuestionAdapter chseAdapter;
    private List<QuestionModelClass> chseList;

    private QuestionAdapter neetAdapter;
    private List<QuestionModelClass> neetList;

    private QuestionAdapter cbseAdapter;
    private List<QuestionModelClass> cbseList;

    private QuestionAdapter nursingAdapter;
    private List<QuestionModelClass> nursingList;

    private QuestionAdapter ouatAdapter;
    private List<QuestionModelClass> ouatList;

    private TextView greetText, userName, rcvTextChse, rcvTextNeet, rcvTextCbse, rcvTextNursing, rcvTextOuat;
    private ImageView userImg;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference mStore;
    private ListenerRegistration userListener;
    private ProgressBar load;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView chseRcv,neetRcv, cbseRcv, nursingRcv, ouatRcv;
    private CardView chseCard, neetCard, cbseCard, nursingCard, ouatCard;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Enable options menu in this fragment
        setHasOptionsMenu(true);

        // Initialize Firebase instances
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        FirebaseUser currentUser = mAuth.getCurrentUser();

        chseCard = view.findViewById(R.id.chseCard);
        neetCard = view.findViewById(R.id.neetCard);
        cbseCard = view.findViewById(R.id.cbseCard);
        nursingCard = view.findViewById(R.id.nursingCard);
        ouatCard = view.findViewById(R.id.ouatCard);

        rcvTextChse = view.findViewById(R.id.chseText);
        rcvTextChse.setSelected(true);
        rcvTextNeet = view.findViewById(R.id.neetText);
        rcvTextNeet.setSelected(true);
        rcvTextCbse = view.findViewById(R.id.cbseText);
        rcvTextCbse.setSelected(true);
        rcvTextNursing = view.findViewById(R.id.nursingText);
        rcvTextNursing.setSelected(true);
        rcvTextOuat = view.findViewById(R.id.ouatText);
        rcvTextOuat.setSelected(true);

        greetText = view.findViewById(R.id.homeTextGmGe);
        userName = view.findViewById(R.id.homeUserName);
        userImg = view.findViewById(R.id.homeUserImg);
        load = view.findViewById(R.id.loadHomeActivity);
        load.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(getActivity(), R.color.app_green),
                android.graphics.PorterDuff.Mode.SRC_IN);
        swipeRefreshLayout = view.findViewById(R.id.swipe_refresh_layout);

        chseRcv = view.findViewById(R.id.chseRecyclerview);
        neetRcv = view.findViewById(R.id.neetRecyclerview);
        cbseRcv = view.findViewById(R.id.cbseRecyclerview);
        nursingRcv = view.findViewById(R.id.nursingRecyclerview);
        ouatRcv = view.findViewById(R.id.ouatRecyclerview);

        chseRcv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        chseList = new ArrayList<>();
        chseAdapter = new QuestionAdapter(getContext(), chseList, load);
        chseRcv.setAdapter(chseAdapter);

        neetRcv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        neetList = new ArrayList<>();
        neetAdapter = new QuestionAdapter(getContext(), neetList, load);
        neetRcv.setAdapter(neetAdapter);

        cbseRcv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        cbseList = new ArrayList<>();
        cbseAdapter = new QuestionAdapter(getContext(), cbseList, load);
        cbseRcv.setAdapter(cbseAdapter);

        nursingRcv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        nursingList = new ArrayList<>();
        nursingAdapter = new QuestionAdapter(getContext(), nursingList, load);
        nursingRcv.setAdapter(nursingAdapter);

        ouatRcv.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        ouatList = new ArrayList<>();
        ouatAdapter = new QuestionAdapter(getContext(), ouatList, load);
        ouatRcv.setAdapter(ouatAdapter);


        // Check if user is authenticated
        if (currentUser != null) {
            String userId = currentUser.getUid();
            mStore = FirebaseStorage.getInstance().getReference();

            fetchUserData(userId);
        } else {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }

        setupNavigationDrawer(view);
        setupToolbar(view);
        setupGreetingMessage();
        setupSwipeRefreshLayout();
        chse();
        neet();
        cbse();
        nursing();
        ouat();

        userImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showImgDialog();
            }
        });

        return view;
    }


    private void chse() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference questionsRef = firestore.collection("CHSE");

        load.setVisibility(View.VISIBLE);

        questionsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Handle the error
                    load.setVisibility(View.GONE);
                    Log.e("Firestore", "Error fetching data", error);
                    return;
                }

                chseList.clear();
                if (value != null) {
                    for (QueryDocumentSnapshot document : value) {
                        QuestionModelClass question = document.toObject(QuestionModelClass.class);
                        chseList.add(question);
                    }
                    chseAdapter.notifyDataSetChanged();

                    if (chseList.isEmpty()) {
                        chseCard.setVisibility(View.GONE); // Hide RecyclerView
                    } else {
                        chseCard.setVisibility(View.VISIBLE); // Show RecyclerView
                    }

                }
                load.setVisibility(View.GONE);
            }
        });
    }

    private void neet() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference questionsRef = firestore.collection("NEET");

        load.setVisibility(View.VISIBLE);

        questionsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Handle the error
                    Log.e("Firestore", "Error fetching data", error);
                    load.setVisibility(View.GONE);
                    return;
                }

                neetList.clear();
                if (value != null) {
                    for (QueryDocumentSnapshot document : value) {
                        QuestionModelClass question = document.toObject(QuestionModelClass.class);
                        neetList.add(question);
                    }
                    neetAdapter.notifyDataSetChanged();

                    if (neetList.isEmpty()) {
                        neetCard.setVisibility(View.GONE); // Hide RecyclerView
                    } else {
                        neetCard.setVisibility(View.VISIBLE); // Show RecyclerView
                    }
                }
                load.setVisibility(View.GONE);
            }
        });
    }

    private void cbse() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference questionsRef = firestore.collection("CBSE");

        load.setVisibility(View.VISIBLE);

        questionsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Handle the error
                    Log.e("Firestore", "Error fetching data", error);
                    load.setVisibility(View.GONE);
                    return;
                }

                cbseList.clear();
                if (value != null) {
                    for (QueryDocumentSnapshot document : value) {
                        QuestionModelClass question = document.toObject(QuestionModelClass.class);
                        cbseList.add(question);
                    }
                    cbseAdapter.notifyDataSetChanged();

                    if (cbseList.isEmpty()) {
                        cbseCard.setVisibility(View.GONE); // Hide RecyclerView
                    } else {
                        cbseCard.setVisibility(View.VISIBLE); // Show RecyclerView
                    }
                }
                load.setVisibility(View.GONE);
            }
        });
    }

    private void nursing() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference questionsRef = firestore.collection("NURSING");

        load.setVisibility(View.VISIBLE);

        questionsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Handle the error
                    Log.e("Firestore", "Error fetching data", error);
                    load.setVisibility(View.GONE);
                    return;
                }

                nursingList.clear();
                if (value != null) {
                    for (QueryDocumentSnapshot document : value) {
                        QuestionModelClass question = document.toObject(QuestionModelClass.class);
                        nursingList.add(question);
                    }
                    nursingAdapter.notifyDataSetChanged();

                    if (nursingList.isEmpty()) {
                        nursingCard.setVisibility(View.GONE); // Hide RecyclerView
                    } else {
                        nursingCard.setVisibility(View.VISIBLE); // Show RecyclerView
                    }
                }
                load.setVisibility(View.GONE);
            }
        });
    }

    private void ouat() {
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        CollectionReference questionsRef = firestore.collection("OUAT");

        load.setVisibility(View.VISIBLE);

        questionsRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    // Handle the error
                    Log.e("Firestore", "Error fetching data", error);
                    load.setVisibility(View.GONE);
                    return;
                }

                ouatList.clear();
                if (value != null) {
                    for (QueryDocumentSnapshot document : value) {
                        QuestionModelClass question = document.toObject(QuestionModelClass.class);
                        ouatList.add(question);
                    }
                    ouatAdapter.notifyDataSetChanged();

                    if (ouatList.isEmpty()) {
                        ouatCard.setVisibility(View.GONE); // Hide RecyclerView
                    } else {
                        ouatCard.setVisibility(View.VISIBLE); // Show RecyclerView
                    }
                }
                load.setVisibility(View.GONE);
            }
        });
    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(() -> {
            if (mAuth.getCurrentUser() != null) {
                String userId = mAuth.getCurrentUser().getUid();
                fetchUserData(userId);
            }
            swipeRefreshLayout.setRefreshing(false);
        });
    }

    private void fetchUserData(String userId) {
        // Listen for changes in the user's document
        userListener = db.collection("users")
                .document(userId)
                .addSnapshotListener((snapshot, e) -> {
                    if (e != null) {
                        Toast.makeText(requireContext(), "Error fetching data", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    if (snapshot != null && snapshot.exists()) {
                        updateUserUI(snapshot);
                    }
                });
    }

    private void updateUserUI(DocumentSnapshot snapshot) {
        // Update the UI with user data
        String userNameStr = snapshot.getString("name");
        String profileImageUrl = snapshot.getString("photoUrl");

        if (userNameStr != null) {
            userName.setText(userNameStr);
        }

        load.setVisibility(View.VISIBLE);

        if (profileImageUrl != null) {
            Glide.with(HomeFragment.this)
                    .load(profileImageUrl)
                    .placeholder(R.drawable.starbiologyprofiledefaultimg)
                    .error(R.drawable.starbiologyprofiledefaultimg)
                    .transition(DrawableTransitionOptions.withCrossFade())
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            // Hide progress bar on error
                            load.setVisibility(View.GONE);
                            return false; // Allow Glide to handle the error placeholder
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            // Hide progress bar when image is ready
                            load.setVisibility(View.GONE);
                            return false; // Allow Glide to handle the resource
                        }
                    })
                    .into(userImg);
        }
    }

    private void setupNavigationDrawer(View view) {
        DrawerLayout drawerLayout = view.findViewById(R.id.drawer_layout);
        NavigationView navigationView = view.findViewById(R.id.navigation_view);
        View headerView = navigationView.getHeaderView(0);

        TextView navHeaderUserName = headerView.findViewById(R.id.getUserNameNav);
        navHeaderUserName.setSelected(true);
        TextView navHeaderEmail = headerView.findViewById(R.id.getEmailNav);
        navHeaderEmail.setSelected(true);
        ImageView navHeaderImg = headerView.findViewById(R.id.getImageNav);

        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String userId = currentUser.getUid();

            // Fetch data from Firestore
            db.collection("users").document(userId).get()
                    .addOnSuccessListener(snapshot -> {
                        if (snapshot.exists()) {
                            String userName = snapshot.getString("name");
                            String email = snapshot.getString("email");
                            String imageUrl = snapshot.getString("photoUrl");

                            navHeaderUserName.setText(userName != null ? userName : "No Name");
                            navHeaderEmail.setText(email != null ? email : "No Email");

                            if (imageUrl != null && !imageUrl.isEmpty()) {
                                Glide.with(HomeFragment.this)
                                        .load(imageUrl)
                                        .transition(DrawableTransitionOptions.withCrossFade())
                                        .listener(new RequestListener<Drawable>() {
                                            @Override
                                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                                load.setVisibility(View.GONE);
                                                Toast.makeText(requireContext(), "Error loading image", Toast.LENGTH_SHORT).show();
                                                return false;
                                            }

                                            @Override
                                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                                load.setVisibility(View.GONE);
                                                return false;
                                            }
                                        })
                                        .into(navHeaderImg);
                            }
                        } else {
                            Toast.makeText(requireContext(), "User data not found", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(requireContext(), "Error loading user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(requireContext(), "User not authenticated", Toast.LENGTH_SHORT).show();
        }

        navigationView.setNavigationItemSelectedListener(item -> {
            item.setChecked(true);
            drawerLayout.closeDrawer(GravityCompat.START);
            handleNavigationItemSelected(item.getItemId());
            return true;
        });
    }

    private void setupToolbar(View view) {
        Toolbar toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) requireActivity()).setSupportActionBar(toolbar);

        DrawerLayout drawerLayout = view.findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                requireActivity(), drawerLayout, toolbar,
                R.string.open_nav, R.string.close_nav);

        // Set custom toggle icon
        toggle.setDrawerIndicatorEnabled(false); // Disable default hamburger icon
        Drawable customIcon = requireContext().getDrawable(R.drawable.baseline_align_horizontal_left_24); // Replace with your drawable
        toolbar.setNavigationIcon(customIcon);

        // Set listener for the custom icon click
        toolbar.setNavigationOnClickListener(view1 -> {
            if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                drawerLayout.closeDrawer(GravityCompat.START);
            } else {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
    }

    private void setupGreetingMessage() {
        // Set greeting message based on the time of day
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        String greeting;
        if (hour >= 5 && hour < 12) {
            greeting = "Good Morning,";
        } else if (hour >= 12 && hour < 17) {
            greeting = "Good Afternoon,";
        } else {
            greeting = "Good Evening,";
        }
        greetText.setText(greeting);
    }

    private void handleNavigationItemSelected(int itemId) {
        // Handle navigation item selection
        if (itemId == R.id.nav_share) {
            shareApp();
        } else if (itemId == R.id.nav_contactUs) {
            contactUs();
        } else if (itemId == R.id.nav_rateUs) {
            rateApp();
        } else if (itemId == R.id.nav_privacyPolicy) {
            privacyPolicy();
        } else if (itemId == R.id.nav_chatWithUs) {
            Toast.makeText(getActivity(), "Added Soon", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.home_activity_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle toolbar menu item clicks
        int id = item.getItemId();
        if (id == R.id.nav_question) {
            startActivity(new Intent(getActivity(), QuestionActivity.class));
        }
        return super.onOptionsItemSelected(item);

    }

    private void privacyPolicy() {
        // Reference to your Firestore document
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
                    Toast.makeText(getActivity(), "Privacy policy URL not found.", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // Handle possible errors
                Toast.makeText(getActivity(), "Failed to load privacy policy.", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void rateApp() {
        try {
            // Create an Intent to open the Play Store
            String packageName = getActivity().getPackageName();
            String playStoreUrl = "https://play.google.com/store/apps/details?id=" + packageName;

            // Start the activity with the Play Store URL
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(playStoreUrl)));
        } catch (ActivityNotFoundException e) {
            // Handle case where the Play Store is not installed or cannot be opened
            e.printStackTrace();
            Toast.makeText(getActivity(), "Unable to open the Play Store. Please try again later.", Toast.LENGTH_SHORT).show();
        }

}

    private void contactUs() {
        String appName = getString(R.string.app_name);
        Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
        emailIntent.setData(Uri.parse("assist.kprgroup@gmail.com"));
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Feedback or Support for" + appName);
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Dear Support Team,\n\n");

        try {
            startActivity(Intent.createChooser(emailIntent, "Send Email"));
        } catch (ActivityNotFoundException e) {
            Toast.makeText(getActivity(), "No email app installed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareApp() {
        // Retrieve the app name and package name
        String appName = getString(R.string.app_name);
        String packageName = getActivity().getApplicationContext().getPackageName();

        // Update the app link to point to Google Play Store
        String appLink = "https://play.google.com/store/apps/details?id=" + packageName;

        // Create an intent to share the app
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TEXT, "Check out this " + appName + " app on Google Play Store: " + appLink);
        shareIntent.setType("text/plain");

        // Check if there is an activity to handle the share intent
        if (shareIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            startActivity(Intent.createChooser(shareIntent, "Share via"));
        }
}

    private void showImgDialog() {
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
        if (userListener != null) {
            userListener.remove(); // Detach Firestore listener to avoid memory leaks
        }
    }
}