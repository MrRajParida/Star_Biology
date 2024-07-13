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


public class BackendNoteFragment extends Fragment {
    private RecyclerView recyclerView;
    private NotePdfAdapter notePdfAdapter;
    private List<NotePdfModelClass> pdfList;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;
    public BackendNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_backend_note, container, false);

        progressBar = view.findViewById(R.id.noteLoadBackendProgressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(getActivity(), R.color.app_color),
                android.graphics.PorterDuff.Mode.SRC_IN);
        recyclerView = view.findViewById(R.id.backendNoteRCV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pdfList = new ArrayList<>();
        notePdfAdapter = new NotePdfAdapter(getContext(), pdfList, progressBar);
        recyclerView.setAdapter(notePdfAdapter);

        fetchZoologyNotes();

        return view;
    }

    private void fetchZoologyNotes() {
        databaseReference = FirebaseDatabase.getInstance().getReference("ZOOLOGYPDFS");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pdfList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    NotePdfModelClass pdfFile = postSnapshot.getValue(NotePdfModelClass.class);
                    if (pdfFile != null) {
                        pdfList.add(pdfFile);
                    }
                }
                notePdfAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}