package com.antech.starbiology.Video;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.antech.starbiology.Adapter.VideoAdapter;
import com.antech.starbiology.ModelClass.VideoModelClass;
import com.antech.starbiology.R;

import java.util.ArrayList;
import java.util.List;


public class VideoBotanyFragment extends Fragment {

    private ImageView noBookmarksMessage;
    private RecyclerView recyclerView;
    private VideoAdapter videoAdapter;
    private List<VideoModelClass> videoList;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;

    public VideoBotanyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_video_botany, container, false);

        noBookmarksMessage = view.findViewById(R.id.noBookmarksMessage);
        progressBar = view.findViewById(R.id.videoLoadBotanyProgressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(getActivity(), R.color.app_green),
                android.graphics.PorterDuff.Mode.SRC_IN);

        recyclerView = view.findViewById(R.id.botanyVideoRCV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        videoList = new ArrayList<>();
        videoAdapter = new VideoAdapter(getContext(), videoList, progressBar);
        recyclerView.setAdapter(videoAdapter);

        firestore = FirebaseFirestore.getInstance();

        fetchDataFromFireStore();

        return view;
    }

    private void fetchDataFromFireStore() {
        CollectionReference videoCollectionRef = firestore.collection("BOTANYVIDEOS");

        progressBar.setVisibility(View.VISIBLE);

        videoCollectionRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                progressBar.setVisibility(View.GONE);

                if (error != null) {
                    Toast.makeText(getContext(), "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                videoList.clear();

                if (value != null) {
                    for (QueryDocumentSnapshot document : value) {
                        VideoModelClass videoFile = document.toObject(VideoModelClass.class);
                        videoList.add(videoFile);
                    }
                    videoAdapter.notifyDataSetChanged();

                    if (videoList.isEmpty()) {
                        noBookmarksMessage.setVisibility(View.VISIBLE); // Show message
                        recyclerView.setVisibility(View.GONE); // Hide RecyclerView
                    } else {
                        noBookmarksMessage.setVisibility(View.GONE); // Hide message
                        recyclerView.setVisibility(View.VISIBLE); // Show RecyclerView
                    }
                }
            }
        });
    }
}