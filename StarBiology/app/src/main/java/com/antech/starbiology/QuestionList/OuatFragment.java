package com.antech.starbiology.QuestionList;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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
import com.antech.starbiology.ModelClass.QuestionModelClass;
import com.antech.starbiology.Adapter.QuestionListAdapter;
import com.antech.starbiology.R;

import java.util.ArrayList;
import java.util.List;


public class OuatFragment extends Fragment {

    private ImageView noBookmarksMessage;
    private RecyclerView recyclerView;
    private QuestionListAdapter questionListAdapter;
    private List<QuestionModelClass> questionModelClassList;
    private ProgressBar progressBar;
    private FirebaseFirestore firestore;
    public OuatFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ouat, container, false);

        noBookmarksMessage = view.findViewById(R.id.noBookmarksMessage);
        progressBar = view.findViewById(R.id.questionLoadProgressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(getActivity(), R.color.app_green),
                android.graphics.PorterDuff.Mode.SRC_IN);

        recyclerView = view.findViewById(R.id.questionListRCV);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        questionModelClassList = new ArrayList<>();
        questionListAdapter = new QuestionListAdapter(getContext(), questionModelClassList, progressBar);
        recyclerView.setAdapter(questionListAdapter);

        firestore = FirebaseFirestore.getInstance();

        fetchDataFromFireStore();

        return view;
    }

    private void fetchDataFromFireStore() {
        CollectionReference botanyPdfRef = firestore.collection("OUAT");

        progressBar.setVisibility(View.VISIBLE);

        botanyPdfRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                progressBar.setVisibility(View.GONE);

                if (error != null) {
                    Toast.makeText(getContext(), "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                    return;
                }

                questionModelClassList.clear();

                if (value != null) {
                    for (QueryDocumentSnapshot document : value) {
                        QuestionModelClass pdfFile = document.toObject(QuestionModelClass.class);
                        questionModelClassList.add(pdfFile);
                    }
                    questionListAdapter.notifyDataSetChanged();

                    if (questionModelClassList.isEmpty()) {
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