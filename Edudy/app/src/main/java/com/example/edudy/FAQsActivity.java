package com.example.edudy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class FAQsActivity extends AppCompatActivity {
    private FAQsAdapter faQsAdapter;
    private List<FAQItem> faqItemList;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private DatabaseReference databaseReference;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_faqs);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressBar = findViewById(R.id.faqsProgressBar);
        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        faqItemList = new ArrayList<>();
        faQsAdapter = new FAQsAdapter(faqItemList, this, progressBar);
        recyclerView.setAdapter(faQsAdapter);

        // Set up SwipeRefreshLayout
        swipeRefreshLayout.setOnRefreshListener(() -> {
            fetchDataFromFirebase();
        });

        fetchDataFromFirebase();
    }

    private void fetchDataFromFirebase() {
        databaseReference = FirebaseDatabase.getInstance().getReference("FAQs");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                faqItemList.clear();
                for (DataSnapshot postSnapshot : snapshot.getChildren()) {
                    FAQItem faqItem = postSnapshot.getValue(FAQItem.class);
                    if (faqItem != null) {
                        faqItemList.add(faqItem);
                    }
                }
                faQsAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false); // Stop the refreshing indicator
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false); // Stop the refreshing indicator
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}