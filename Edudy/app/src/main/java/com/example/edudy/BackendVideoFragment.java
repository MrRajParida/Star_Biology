package com.example.edudy;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;


public class BackendVideoFragment extends Fragment {
    private RecyclerView recyclerView;
    private VideoViewAdapter videoViewAdapter;
    private List<VideoViewModelClass> videoList;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    public BackendVideoFragment() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_backend_video, container, false);

        progressBar = view.findViewById(R.id.videoLoadBackendProgressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(getActivity(), R.color.app_color),
                android.graphics.PorterDuff.Mode.SRC_IN);
        recyclerView = view.findViewById(R.id.backendVideoRCV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        videoList = new ArrayList<>();
        videoViewAdapter = new VideoViewAdapter(getContext(), videoList, progressBar);
        recyclerView.setAdapter(videoViewAdapter);

        fetchZoologyVideos();

        return view;
    }

    private void fetchZoologyVideos() {

        databaseReference = FirebaseDatabase.getInstance().getReference("ZOOLOGYVIDEOS");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                videoList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    VideoViewModelClass videoFile = postSnapshot.getValue(VideoViewModelClass.class);
                    if (videoFile != null) {
                        videoList.add(videoFile);
                    }
                }
                videoViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}