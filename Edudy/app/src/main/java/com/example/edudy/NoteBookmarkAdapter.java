package com.example.edudy;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NoteBookmarkAdapter extends RecyclerView.Adapter<NoteBookmarkAdapter.NoteBookmarkViewHolder> {
    private Context context;
    private List<NoteBookmarkModelClass> noteBookmarkModelClassList;
    private ProgressBar progressBar;

    public NoteBookmarkAdapter(Context context, List<NoteBookmarkModelClass> noteBookmarkModelClassList, ProgressBar progressBar) {
        this.context = context;
        this.noteBookmarkModelClassList = noteBookmarkModelClassList;
        this.progressBar = progressBar;
    }

    @NonNull
    @Override
    public NoteBookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.notebookmark_list_item, parent, false);
        return new NoteBookmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteBookmarkViewHolder holder, int position) {
        NoteBookmarkModelClass bookmarkItem = noteBookmarkModelClassList.get(position);
        holder.subTitle.setText(bookmarkItem.getBookedTopic());

        Glide.with(context)
                .load(bookmarkItem.getBookedImg())
                .placeholder(R.drawable.noteerrorimage)
                .error(R.drawable.noteerrorimage)
                .into(holder.subImg);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, NotePdfViewActivity.class);
            intent.putExtra("pdfUrl", bookmarkItem.getBookedUrl());
            context.startActivity(intent);
        });

        holder.deleteBookmarkButton.setOnClickListener(view -> {
            deleteBookmark(holder, bookmarkItem);
        });

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return noteBookmarkModelClassList.size();
    }

    public void loadBookmarks() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userBookmarkRef = FirebaseDatabase.getInstance().getReference("EdudyUsers").child(userId).child("NoteBookmarks");

        userBookmarkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                noteBookmarkModelClassList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    NoteBookmarkModelClass bookmark = dataSnapshot.getValue(NoteBookmarkModelClass.class);
                    if (bookmark != null) {
                        noteBookmarkModelClassList.add(bookmark);
                    }
                }
                notifyDataSetChanged();
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error loading bookmarks: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                if (progressBar != null) {
                    progressBar.setVisibility(View.GONE);
                }
            }
        });
    }
    private void deleteBookmark(NoteBookmarkViewHolder holder, NoteBookmarkModelClass bookmarkItem) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userBookmarkRef = FirebaseDatabase.getInstance().getReference("EdudyUsers").child(userId).child("NoteBookmarks");

        // Assuming 'bookedLink' is the unique identifier for the bookmark
        userBookmarkRef.orderByChild("bookedUrl").equalTo(bookmarkItem.getBookedUrl()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        dataSnapshot.getRef().removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                noteBookmarkModelClassList.remove(bookmarkItem); // Remove from local list
                                notifyDataSetChanged(); // Refresh adapter
                                Toast.makeText(context, "Bookmark removed", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Failed to remove bookmark", Toast.LENGTH_SHORT).show();
                            }// Hide progress bar
                        });
                    }
                } else {
                    Toast.makeText(context, "Bookmark not found", Toast.LENGTH_SHORT).show();// Hide progress bar
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();// Hide progress bar
            }
        });
    }

    public static class NoteBookmarkViewHolder extends RecyclerView.ViewHolder {
        TextView subTitle;
        ImageView subImg;
        ImageButton deleteBookmarkButton;

        public NoteBookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            subTitle = itemView.findViewById(R.id.bookedSubTitle);
            subImg = itemView.findViewById(R.id.bookedSubImg);
            deleteBookmarkButton = itemView.findViewById(R.id.deleteBookmarkNoteBtn);
        }
    }
}