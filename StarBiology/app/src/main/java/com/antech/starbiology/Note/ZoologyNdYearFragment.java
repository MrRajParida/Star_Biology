package com.antech.starbiology.Note;

import android.os.Bundle;

import androidx.annotation.NonNull;
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

import com.antech.starbiology.Adapter.NoteAdapter;
import com.antech.starbiology.ModelClass.NoteModelClass;
import com.antech.starbiology.R;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


public class ZoologyNdYearFragment extends Fragment {
    private ImageView noBookmarksMessage;
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private List<NoteModelClass> noteList;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    public ZoologyNdYearFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_zoology_nd_year, container, false);

        progressBar = view.findViewById(R.id.noteLoadZoologyProgressBar);

        // Ensure getActivity() is not null
        if (getActivity() != null) {
            progressBar.getIndeterminateDrawable().setColorFilter(
                    ContextCompat.getColor(getActivity(), R.color.app_green),
                    android.graphics.PorterDuff.Mode.SRC_IN);
        }

        noBookmarksMessage = view.findViewById(R.id.noBookmarksMessage);
        recyclerView = view.findViewById(R.id.zoologyNoteRCV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        noteList = new ArrayList<>();
        noteAdapter = new NoteAdapter(getContext(), noteList, progressBar);
        recyclerView.setAdapter(noteAdapter);

        firestore = FirebaseFirestore.getInstance();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Fetch data after the view has been created
        fetchDataFromFireStore();
    }

    private void fetchDataFromFireStore() {
        CollectionReference zoologyPdfRef = firestore.collection("ZOOLOGYNDYEARNOTES");

        // Ensure the progress bar is visible when data loading starts
        progressBar.setVisibility(View.VISIBLE);

        zoologyPdfRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                if (error != null) {
                    // Use requireContext() to ensure the context is non-null
                    Toast.makeText(requireContext(), "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    // Hide the progress bar on error
                    progressBar.setVisibility(View.GONE);
                    return;
                }

                noteList.clear();

                if (value != null) {
                    for (QueryDocumentSnapshot document : value) {
                        NoteModelClass pdfFile = document.toObject(NoteModelClass.class);
                        noteList.add(pdfFile);
                    }
                    noteAdapter.notifyDataSetChanged();

                    if (noteList.isEmpty()) {
                        noBookmarksMessage.setVisibility(View.VISIBLE); // Show message
                        recyclerView.setVisibility(View.GONE); // Hide RecyclerView
                    } else {
                        noBookmarksMessage.setVisibility(View.GONE); // Hide message
                        recyclerView.setVisibility(View.VISIBLE); // Show RecyclerView
                    }
                }

                // Hide the progress bar once data is loaded
                progressBar.setVisibility(View.GONE);
            }
        });
    }
}