package com.antech.starbiology.Note;

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

public class BotanyStYearFragment extends Fragment {
    private ImageView noBookmarksMessage;
    private RecyclerView recyclerView;
    private NoteAdapter noteAdapter;
    private List<NoteModelClass> noteList;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    public BotanyStYearFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_botany_st_year, container, false);

        noBookmarksMessage = view.findViewById(R.id.noBookmarksMessage);
        progressBar = view.findViewById(R.id.noteLoadBotanyProgressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(getActivity(), R.color.app_green),
                android.graphics.PorterDuff.Mode.SRC_IN);

        recyclerView = view.findViewById(R.id.botanyNoteRCV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        noteList = new ArrayList<>();
        noteAdapter = new NoteAdapter(getContext(), noteList, progressBar);
        recyclerView.setAdapter(noteAdapter);

        firestore = FirebaseFirestore.getInstance();

        fetchDataFromFireStore();

        return view;
    }

    private void fetchDataFromFireStore() {
        CollectionReference botanyPdfRef = firestore.collection("BOTANYSTYEARNOTES");

        progressBar.setVisibility(View.VISIBLE);

        botanyPdfRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                progressBar.setVisibility(View.GONE);

                if (error != null) {
                    Toast.makeText(getContext(), "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
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
            }
        });
    }
}