package com.antech.starbiology;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class QuizSolutionActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private QuizSolutionAdapter adapter;
    private List<QuizSolutionModelClass> questionAnswers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_solution);


        recyclerView = findViewById(R.id.recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        questionAnswers = getIntent().getParcelableArrayListExtra("QuizSolutions");
        if (questionAnswers == null) {
            questionAnswers = new ArrayList<>();
        }

        adapter = new QuizSolutionAdapter(this, questionAnswers);
        recyclerView.setAdapter(adapter);

    }
}