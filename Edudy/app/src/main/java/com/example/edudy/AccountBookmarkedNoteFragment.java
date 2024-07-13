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
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class AccountBookmarkedNoteFragment extends Fragment {
    private RecyclerView recyclerView;
    private List<NoteBookmarkModelClass> bookmarkList;
    private NoteBookmarkAdapter bookmarkAdapter;
    private ProgressBar bookmarkProgressBar;
    private TextView noBookmarksMessage;
    public AccountBookmarkedNoteFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account_bookmark_note, container, false);

        noBookmarksMessage = view.findViewById(R.id.noBookmarksMessage);
        bookmarkProgressBar = view.findViewById(R.id.noteBookmarkProgressBar);
        bookmarkProgressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(getActivity(), R.color.app_color),
                android.graphics.PorterDuff.Mode.SRC_IN);
        recyclerView = view.findViewById(R.id.noteBookmarkRCV);
        bookmarkList = new ArrayList<>();
        bookmarkAdapter = new NoteBookmarkAdapter(getContext(), bookmarkList, bookmarkProgressBar);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(bookmarkAdapter);

        fetchBookmarks();

        return view;
    }

    private void fetchBookmarks() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference bookmarkRef = FirebaseDatabase.getInstance().getReference("EdudyUsers").child(userId).child("NoteBookmarks");

        bookmarkRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookmarkList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    NoteBookmarkModelClass bookmark = dataSnapshot.getValue(NoteBookmarkModelClass.class);
                    if (bookmark != null) {
                        bookmarkList.add(bookmark);
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
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                bookmarkProgressBar.setVisibility(View.GONE);
                Toast.makeText(getActivity(), "Failed to load bookmarks: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}