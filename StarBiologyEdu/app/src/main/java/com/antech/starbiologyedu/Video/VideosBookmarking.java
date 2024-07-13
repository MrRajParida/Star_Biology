package com.antech.starbiologyedu.Video;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.starproduction.starbiology.Activity.LoginActivity;
import com.starproduction.starbiology.Adapter.VideoBookmarkAdapter;
import com.starproduction.starbiology.ModelClass.VideoBookmarkModelClass;
import com.starproduction.starbiology.R;

import java.util.ArrayList;
import java.util.List;

public class VideosBookmarking extends Fragment {
    private RecyclerView recyclerView;
    private List<VideoBookmarkModelClass> bookmarkList;
    private VideoBookmarkAdapter bookmarkAdapter;
    private ProgressBar bookmarkProgressBar;
    private ImageView noBookmarksMessage;
    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistration;

    public VideosBookmarking() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_videos_bookmarking, container, false);

        noBookmarksMessage = view.findViewById(R.id.noBookmarksMessage);
        bookmarkProgressBar = view.findViewById(R.id.videoBookmarkProgressBar);
        bookmarkProgressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(getActivity(), R.color.app_green),
                android.graphics.PorterDuff.Mode.SRC_IN);
        recyclerView = view.findViewById(R.id.videoBookmarkRCV);
        bookmarkList = new ArrayList<>();
        bookmarkAdapter = new VideoBookmarkAdapter(getContext(), bookmarkList, bookmarkProgressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(bookmarkAdapter);

        fetchBookmarks();

        return view;
    }

    private void fetchBookmarks() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
            return;
        }

        String userId = currentUser.getUid();
        db = FirebaseFirestore.getInstance();

        CollectionReference bookmarkRef = db.collection("users").document(userId).collection("VideoBookmarks");

        listenerRegistration = bookmarkRef.addSnapshotListener((QuerySnapshot snapshot, FirebaseFirestoreException e) -> {
            if (e != null) {
                // Handle any errors
                bookmarkProgressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Failed to load bookmarks: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            bookmarkList.clear();
            if (snapshot != null) {
                for (QueryDocumentSnapshot document : snapshot) {
                    VideoBookmarkModelClass bookmark = document.toObject(VideoBookmarkModelClass.class);
                    if (bookmark != null) {
                        bookmarkList.add(bookmark);
                    }
                }
            }
            bookmarkAdapter.notifyDataSetChanged();

            if (bookmarkList.isEmpty()) {
                noBookmarksMessage.setVisibility(View.VISIBLE); // Show message
                recyclerView.setVisibility(View.GONE); // Hide RecyclerView
            } else {
                noBookmarksMessage.setVisibility(View.GONE); // Hide message
                recyclerView.setVisibility(View.VISIBLE); // Show RecyclerView
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}