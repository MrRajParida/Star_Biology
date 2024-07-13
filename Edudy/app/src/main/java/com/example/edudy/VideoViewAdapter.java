package com.example.edudy;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
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


public class VideoViewAdapter extends RecyclerView.Adapter<VideoViewAdapter.VideoViewHolder> {
    private Context context;
    private List<VideoViewModelClass> videoViewModelClassList;
    private ProgressBar progressBar;

    public VideoViewAdapter(Context context, List<VideoViewModelClass> videoViewModelClassList, ProgressBar progressBar) {
        this.context = context;
        this.videoViewModelClassList = videoViewModelClassList;
        this.progressBar = progressBar;
    }

    @NonNull
    @Override
    public VideoViewAdapter.VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_list_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewAdapter.VideoViewHolder holder, int position) {
        VideoViewModelClass viewModel = videoViewModelClassList.get(position);

        holder.textTitle.setText(viewModel.getTitle());
        holder.textDescription.setText(viewModel.getDescription());

        Glide.with(context)
                .load(viewModel.getThumbnail())
                .placeholder(R.drawable.videoerroeimg)
                .error(R.drawable.videoerroeimg)
                .into(holder.imgThumbnail);

        // Update the bookmark icon based on whether the video is bookmarked or not
        updateBookmarkIcon(holder.bookmarkButton, holder.bookmarkProgressBar, viewModel.getLink());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, VideoViewActivity.class);
            intent.putExtra("videolink", viewModel.getLink());
            context.startActivity(intent);
        });

        holder.bookmarkButton.setOnClickListener(view -> handleBookmarkAction(holder.bookmarkButton, holder.bookmarkProgressBar, viewModel));

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return videoViewModelClassList.size();
    }

    private void updateBookmarkIcon(ImageButton bookmarkButton, ProgressBar bookmarkProgressBar, String videoLink) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userBookmarkRef = FirebaseDatabase.getInstance().getReference("EdudyUsers")
                .child(userId).child("VideoBookmarks");

        userBookmarkRef.orderByChild("bookedLink").equalTo(videoLink).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    bookmarkButton.setImageResource(R.drawable.baseline_bookmark_24);
                } else {
                    bookmarkButton.setImageResource(R.drawable.baseline_bookmark_border_24);
                }
                bookmarkProgressBar.setVisibility(View.GONE);
                bookmarkButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error checking bookmark: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                bookmarkProgressBar.setVisibility(View.GONE);
                bookmarkButton.setVisibility(View.VISIBLE);
            }
        });
    }

    private void handleBookmarkAction(ImageButton bookmarkButton, ProgressBar bookmarkProgressBar, VideoViewModelClass viewModel) {
        bookmarkButton.setVisibility(View.GONE);
        bookmarkProgressBar.setVisibility(View.VISIBLE);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userBookmarkRef = FirebaseDatabase.getInstance().getReference("EdudyUsers")
                .child(userId).child("VideoBookmarks");

        userBookmarkRef.orderByChild("bookedLink").equalTo(viewModel.getLink()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String bookmarkId = dataSnapshot.getKey();
                        userBookmarkRef.child(bookmarkId).removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Bookmark removed", Toast.LENGTH_SHORT).show();
                                    bookmarkButton.setImageResource(R.drawable.baseline_bookmark_border_24);
                                    bookmarkButton.setVisibility(View.VISIBLE);
                                    bookmarkProgressBar.setVisibility(View.GONE);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to remove bookmark: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    bookmarkButton.setVisibility(View.VISIBLE);
                                    bookmarkProgressBar.setVisibility(View.GONE);
                                });
                    }
                } else {
                    // Video is not bookmarked, so add bookmark
                    String bookmarkId = userBookmarkRef.push().getKey();
                    Map<String, Object> bookmarkMap = new HashMap<>();
                    bookmarkMap.put("bookedThumbnail", viewModel.getThumbnail());
                    bookmarkMap.put("bookedTitle", viewModel.getTitle());
                    bookmarkMap.put("bookedLink", viewModel.getLink());

                    userBookmarkRef.child(bookmarkId).setValue(bookmarkMap)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Bookmark added", Toast.LENGTH_SHORT).show();
                                bookmarkButton.setImageResource(R.drawable.baseline_bookmark_24);
                                bookmarkButton.setVisibility(View.VISIBLE);
                                bookmarkProgressBar.setVisibility(View.GONE);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Failed to add bookmark: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                bookmarkButton.setVisibility(View.VISIBLE);
                                bookmarkProgressBar.setVisibility(View.GONE);
                            });
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error handling bookmark: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                bookmarkButton.setVisibility(View.VISIBLE);
                bookmarkProgressBar.setVisibility(View.GONE);
            }
        });
    }
    public static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;
        TextView textTitle, textDescription;
        ImageButton bookmarkButton;
        ProgressBar bookmarkProgressBar;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.thumbnail_img);
            textTitle = itemView.findViewById(R.id.title_thumbnail);
            textDescription = itemView.findViewById(R.id.description_thumbnail);
            bookmarkButton = itemView.findViewById(R.id.videoBookmarkBtn);
            bookmarkProgressBar = itemView.findViewById(R.id.bookmarkProgressBar);
        }
    }
}