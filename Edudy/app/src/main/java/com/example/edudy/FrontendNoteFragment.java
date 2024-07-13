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


public class FrontendNoteFragment extends Fragment {

    private RecyclerView recyclerView;
    private NotePdfAdapter notePdfAdapter;
    private List<NotePdfModelClass> pdfList;
    private ProgressBar progressBar;
    public FrontendNoteFragment() {
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_frontend_note, container, false);

        progressBar = view.findViewById(R.id.noteLoadFrontendProgressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(getActivity(), R.color.app_color),
                android.graphics.PorterDuff.Mode.SRC_IN);
        recyclerView = view.findViewById(R.id.frontendNoteRCV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        pdfList = new ArrayList<>();
        notePdfAdapter = new NotePdfAdapter(getContext(), pdfList, progressBar);
        recyclerView.setAdapter(notePdfAdapter);
        fetchDataFromRealtimeDatabase();

        return view;
    }

    private void fetchDataFromRealtimeDatabase() {
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("BOTANYPDFS");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                pdfList.clear();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    NotePdfModelClass pdfFile = postSnapshot.getValue(NotePdfModelClass.class);
                    if (pdfFile != null) {
                        pdfList.add(pdfFile);
                    }
                }
                notePdfAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to load data", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
