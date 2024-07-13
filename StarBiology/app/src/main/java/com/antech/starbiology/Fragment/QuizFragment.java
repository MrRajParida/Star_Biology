package com.antech.starbiology.Fragment;

import android.os.Bundle;

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

import com.antech.starbiology.Adapter.QuizCatagoryAdapter;
import com.antech.starbiology.ModelClass.QuizModel;
import com.antech.starbiology.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class QuizFragment extends Fragment {
    private ImageView noCategoriesMessage;
    private RecyclerView recyclerView;
    private QuizCatagoryAdapter adapter;
    private List<QuizModel> quizCategoryList;
    private DatabaseReference databaseReference;
    private ProgressBar progressBar;

    public QuizFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_quiz, container, false);

        noCategoriesMessage = view.findViewById(R.id.noCatagoryMessage);
        recyclerView = view.findViewById(R.id.recyclerView);
        progressBar = view.findViewById(R.id.loading);
        progressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(getActivity(), R.color.app_green),
                android.graphics.PorterDuff.Mode.SRC_IN
        );

        databaseReference = FirebaseDatabase.getInstance().getReference("quizCategory");
        quizCategoryList = new ArrayList<>();

        adapter = new QuizCatagoryAdapter(getContext(), quizCategoryList, progressBar);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(adapter);

        loadQuizCategories();

        return view;
    }

    private void loadQuizCategories() {
        progressBar.setVisibility(View.VISIBLE);

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                progressBar.setVisibility(View.GONE);
                quizCategoryList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    QuizModel category = snapshot.getValue(QuizModel.class);

                    if (category != null) {
                        quizCategoryList.add(category);
                    } else {
                        Toast.makeText(getContext(), "Failed to parse category", Toast.LENGTH_SHORT).show();
                    }
                }

                adapter.notifyDataSetChanged();


                if (quizCategoryList.isEmpty()) {
                    noCategoriesMessage.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    noCategoriesMessage.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Handle any errors
                progressBar.setVisibility(View.GONE);
                Toast.makeText(getContext(), "Failed to load data: " + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
