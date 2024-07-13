package com.antech.starbiology.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.antech.starbiology.R;
import com.antech.starbiology.ModelClass.VideoModelClass;
import com.antech.starbiology.Activity.VideoViewActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {
    private Context context;
    private List<VideoModelClass> videoModelClassList;
    private ProgressBar globalProgressBar; // Single ProgressBar for loading and bookmarking
    private FirebaseFirestore firestore;
    private FirebaseUser currentUser;

    public VideoAdapter(Context context, List<VideoModelClass> videoModelClassList, ProgressBar globalProgressBar) {
        this.context = context;
        this.videoModelClassList = videoModelClassList;
        this.globalProgressBar = globalProgressBar;
        this.firestore = FirebaseFirestore.getInstance();
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.video_list, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoModelClass viewModel = videoModelClassList.get(position);

        holder.textTitle.setText(viewModel.getTitle());
        holder.textDescription.setText(viewModel.getDescription());

        Glide.with(context)
                .load(viewModel.getThumbnail())
                .placeholder(R.drawable.starbiologyvideodefaultimg)
                .error(R.drawable.starbiologyvideodefaultimg)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Log the error
                        Log.e("GlideError", "Image load failed for URL: " + viewModel.getThumbnail(), e);

                        // Optionally, show a toast message
                        Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show();

                        // Returning false allows the error placeholder to be set
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // Image loaded successfully
                        Log.d("GlideSuccess", "Image loaded successfully for URL: " + viewModel.getThumbnail());

                        // Returning false allows Glide to handle the view
                        return false;
                    }
                })
                .into(holder.imgThumbnail);

        // Update the bookmark icon based on whether the video is bookmarked or not
        updateBookmarkIcon(holder.bookmarkButton, holder.bookmarkProgressBar, viewModel.getLink());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, VideoViewActivity.class);
            intent.putExtra("videoLink", viewModel.getLink());
            intent.putExtra("videoTitle", viewModel.getTitle());
            intent.putExtra("videoDesc", viewModel.getDescription());
            context.startActivity(intent);
        });

        holder.bookmarkButton.setOnClickListener(view -> handleBookmarkAction(holder.bookmarkButton, holder.bookmarkProgressBar, viewModel));

        // Hide global progress bar once the item is bound
        if (globalProgressBar != null) {
            globalProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return videoModelClassList.size();
    }

    private void updateBookmarkIcon(ImageButton bookmarkButton, ProgressBar bookmarkProgressBar, String videoLink) {
        if (currentUser == null) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        CollectionReference userBookmarkRef = firestore.collection("users")
                .document(userId).collection("VideoBookmarks");

        showGlobalProgressBar(); // Show the global progress bar

        userBookmarkRef.whereEqualTo("bookedLink", videoLink).get()
                .addOnCompleteListener(task -> {
                    hideGlobalProgressBar(); // Hide the global progress bar
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null && !snapshot.isEmpty()) {
                            bookmarkButton.setImageResource(R.drawable.baseline_bookmark_24);
                        } else {
                            bookmarkButton.setImageResource(R.drawable.baseline_bookmark_border_24);
                        }
                    } else {
                        Toast.makeText(context, "Error checking bookmark: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                    bookmarkProgressBar.setVisibility(View.GONE);
                    bookmarkButton.setVisibility(View.VISIBLE);
                });
    }

    private void handleBookmarkAction(ImageButton bookmarkButton, ProgressBar bookmarkProgressBar, VideoModelClass viewModel) {
        bookmarkButton.setVisibility(View.GONE);
        bookmarkProgressBar.setVisibility(View.VISIBLE);

        if (currentUser == null) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        CollectionReference userBookmarkRef = firestore.collection("users")
                .document(userId).collection("VideoBookmarks");

        showGlobalProgressBar(); // Show the global progress bar

        userBookmarkRef.whereEqualTo("bookedLink", viewModel.getLink()).get()
                .addOnCompleteListener(task -> {
                    hideGlobalProgressBar(); // Hide the global progress bar
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null && !snapshot.isEmpty()) {
                            for (DocumentSnapshot document : snapshot.getDocuments()) {
                                document.getReference().delete()
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
                            DocumentReference newBookmarkRef = userBookmarkRef.document();
                            Map<String, Object> bookmarkMap = new HashMap<>();
                            bookmarkMap.put("bookedThumbnail", viewModel.getThumbnail());
                            bookmarkMap.put("bookedTitle", viewModel.getTitle());
                            bookmarkMap.put("bookedLink", viewModel.getLink());

                            newBookmarkRef.set(bookmarkMap)
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
                    } else {
                        Toast.makeText(context, "Error handling bookmark: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        bookmarkButton.setVisibility(View.VISIBLE);
                        bookmarkProgressBar.setVisibility(View.GONE);
                    }
                });
    }

    private void showGlobalProgressBar() {
        if (globalProgressBar != null) {
            globalProgressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideGlobalProgressBar() {
        if (globalProgressBar != null) {
            globalProgressBar.setVisibility(View.GONE);
        }
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
