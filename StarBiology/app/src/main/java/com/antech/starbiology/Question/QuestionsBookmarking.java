package com.antech.starbiology.Question;

import android.content.Intent;
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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.antech.starbiology.Activity.LoginActivity;
import com.antech.starbiology.Adapter.QuestionBookmarkAdapter;
import com.antech.starbiology.ModelClass.QuestionBookmarkModelClass;
import com.antech.starbiology.R;

import java.util.ArrayList;
import java.util.List;

public class QuestionsBookmarking extends Fragment {
    private RecyclerView recyclerView;
    private List<QuestionBookmarkModelClass> bookmarkList;
    private QuestionBookmarkAdapter bookmarkAdapter;
    private ProgressBar bookmarkProgressBar;
    private ImageView noBookmarksMessage;
    private FirebaseFirestore db;
    private ListenerRegistration listenerRegistration;

    public QuestionsBookmarking() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_questions_bookmarking, container, false);


        noBookmarksMessage = view.findViewById(R.id.noBookmarksMessage);
        bookmarkProgressBar = view.findViewById(R.id.questionBookmarkProgressBar);
        bookmarkProgressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(getActivity(), R.color.app_green),
                android.graphics.PorterDuff.Mode.SRC_IN);
        recyclerView = view.findViewById(R.id.questionBookmarkRCV);
        bookmarkList = new ArrayList<>();
        bookmarkAdapter = new QuestionBookmarkAdapter(getContext(), bookmarkList, bookmarkProgressBar);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        recyclerView.setAdapter(bookmarkAdapter);

        fetchBookmarks();

        return view;
    }

    private void fetchBookmarks() {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser == null) {
            // Handle case where user is not logged in
            Toast.makeText(getActivity(), "User not logged in", Toast.LENGTH_SHORT).show();
            // Optionally redirect to login activity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish(); // Optionally close the current activity
            return;
        }

        String userId = currentUser.getUid();
        db = FirebaseFirestore.getInstance();

        CollectionReference bookmarkRef = db.collection("users").document(userId).collection("QuestionBookmarks");

        listenerRegistration = bookmarkRef.addSnapshotListener((QuerySnapshot snapshot, FirebaseFirestoreException e) -> {
            if (e != null) {
                // Handle any errors
                bookmarkProgressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Failed to load bookmarks: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                return;
            }

            bookmarkList.clear();
            if (snapshot != null) {
                for (QueryDocumentSnapshot document : snapshot) {
                    QuestionBookmarkModelClass bookmark = document.toObject(QuestionBookmarkModelClass.class);
                    if (bookmark != null) {
                        bookmarkList.add(bookmark);
                    }
                }
            }
            bookmarkAdapter.notifyDataSetChanged();

            if (bookmarkList.isEmpty()) {
                noBookmarksMessage.setVisibility(View.VISIBLE); // Show message
                recyclerView.setVisibility(View.GONE); // Hide RecyclerView
            } else {
                noBookmarksMessage.setVisibility(View.GONE); // Hide message
                recyclerView.setVisibility(View.VISIBLE); // Show RecyclerView
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        FirebaseUser currentUser = auth.getCurrentUser();
        if (currentUser == null) {
            // Optionally redirect to login activity
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish(); // Optionally close the current activity
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (listenerRegistration != null) {
            listenerRegistration.remove();
        }
    }
}