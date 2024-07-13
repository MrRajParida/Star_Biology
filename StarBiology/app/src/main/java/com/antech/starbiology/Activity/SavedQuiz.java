package com.antech.starbiology.Activity;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.antech.starbiology.Adapter.SaveQuizAdapter;
import com.antech.starbiology.ModelClass.SaveQuizModelClass;
import com.antech.starbiology.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class SavedQuiz extends AppCompatActivity {
    private RecyclerView quizRecyclerView;
    private SaveQuizAdapter quizAdapter;
    private FirebaseFirestore db;
    private List<SaveQuizModelClass> quizList;
    private ProgressBar progressBar;
    private ImageView noBookmarksMessage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_quiz);

        // Setup Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Enable back navigation
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24); // Set custom back icon if needed
        }

        noBookmarksMessage = findViewById(R.id.noBookmarksMessage);
        progressBar = findViewById(R.id.noteLoadQuizProgressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.app_green),
                android.graphics.PorterDuff.Mode.SRC_IN);

        quizRecyclerView = findViewById(R.id.quizQuestionRecyclerview);
        db = FirebaseFirestore.getInstance();
        quizList = new ArrayList<>();
        quizAdapter = new SaveQuizAdapter(quizList, this);

        quizRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        quizRecyclerView.setAdapter(quizAdapter);

        fetchQuizData();
    }

    private void fetchQuizData() {
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        String uid = firebaseAuth.getCurrentUser().getUid();
        CollectionReference quizRef = db.collection("users").document(uid).collection("QuizBookmarked");

        progressBar.setVisibility(View.VISIBLE);

        quizRef.addSnapshotListener((value, error) -> {
            progressBar.setVisibility(View.GONE);

            if (error != null) {
                Toast.makeText(this, "Failed to load data: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            quizList.clear();

            if (value != null) {
                for (QueryDocumentSnapshot document : value) {
                    SaveQuizModelClass quiz = document.toObject(SaveQuizModelClass.class);
                    quiz.setDocumentId(document.getId());
                    quizList.add(quiz);
                }

                quizAdapter.notifyDataSetChanged();

                if (quizList.isEmpty()) {
                    noBookmarksMessage.setVisibility(View.VISIBLE);
                    quizRecyclerView.setVisibility(View.GONE);
                } else {
                    noBookmarksMessage.setVisibility(View.GONE);
                    quizRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle back button in toolbar
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed(); // Call back to the previous activity
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
