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

import java.util.List;

public class VideoBookmarkAdapter extends RecyclerView.Adapter<VideoBookmarkAdapter.VideoBookmarkViewHolder> {
    private Context context;
    private List<VideoBookmarkViewModelClass> bookmarkList;
    private ProgressBar progressBar;

    public VideoBookmarkAdapter(Context context, List<VideoBookmarkViewModelClass> bookmarkList, ProgressBar progressBar) {
        this.context = context;
        this.bookmarkList = bookmarkList;
        this.progressBar = progressBar;
    }

    @NonNull
    @Override
    public VideoBookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.videobookmark_list_item, parent, false);
        return new VideoBookmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoBookmarkViewHolder holder, int position) {
        VideoBookmarkViewModelClass bookmark = bookmarkList.get(position);

        holder.bookmarkTitle.setText(bookmark.getBookedTitle());

        Glide.with(context)
                .load(bookmark.getBookedThumbnail())
                .placeholder(R.drawable.videoerroeimg)
                .error(R.drawable.videoerroeimg)
                .into(holder.bookmarkThumbnail);

        // Set click listener to play the video
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, VideoViewActivity.class);
            intent.putExtra("videolink", bookmark.getBookedLink());
            context.startActivity(intent);
        });

        // Set click listener for the delete button
        holder.bookmarkDeleteButton.setOnClickListener(view -> {
            deleteBookmark(holder, bookmark);
        });

        // Hide progress bar if not null
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }

    public void loadBookmarks() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userBookmarkRef = FirebaseDatabase.getInstance().getReference("EdudyUsers").child(userId).child("VideoBookmarks");

        userBookmarkRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookmarkList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    VideoBookmarkViewModelClass bookmark = dataSnapshot.getValue(VideoBookmarkViewModelClass.class);
                    if (bookmark != null) {
                        bookmarkList.add(bookmark);
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

    private void deleteBookmark(VideoBookmarkViewHolder holder, VideoBookmarkViewModelClass bookmark) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userBookmarkRef = FirebaseDatabase.getInstance().getReference("EdudyUsers").child(userId).child("VideoBookmarks");

        // Assuming 'bookedLink' is the unique identifier for the bookmark
        userBookmarkRef.orderByChild("bookedLink").equalTo(bookmark.getBookedLink()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        dataSnapshot.getRef().removeValue().addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                bookmarkList.remove(bookmark); // Remove from local list
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

    public static class VideoBookmarkViewHolder extends RecyclerView.ViewHolder {
        ImageView bookmarkThumbnail;
        TextView bookmarkTitle;
        ImageButton bookmarkDeleteButton;
        public VideoBookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            bookmarkThumbnail = itemView.findViewById(R.id.bookmark_thumbnail);
            bookmarkTitle = itemView.findViewById(R.id.bookmark_title);
            bookmarkDeleteButton = itemView.findViewById(R.id.bookmark_delete_button);
        }
    }
}