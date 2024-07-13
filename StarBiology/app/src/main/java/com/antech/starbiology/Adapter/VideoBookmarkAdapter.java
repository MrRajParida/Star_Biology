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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.antech.starbiology.R;
import com.antech.starbiology.ModelClass.VideoBookmarkModelClass;
import com.antech.starbiology.Activity.VideoViewActivity;

import java.util.List;

public class VideoBookmarkAdapter extends RecyclerView.Adapter<VideoBookmarkAdapter.VideoViewHolder> {

    private final Context context;
    private final List<VideoBookmarkModelClass> bookmarkList;
    private final ProgressBar globalProgressBar; // Single ProgressBar for loading and deleting
    private final FirebaseFirestore firestore;
    private final FirebaseUser currentUser;

    // Constructor
    public VideoBookmarkAdapter(Context context, List<VideoBookmarkModelClass> bookmarkList, ProgressBar globalProgressBar) {
        this.context = context;
        this.bookmarkList = bookmarkList;
        this.globalProgressBar = globalProgressBar;
        this.firestore = FirebaseFirestore.getInstance();
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public VideoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.videobookmark_list_item, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VideoViewHolder holder, int position) {
        VideoBookmarkModelClass bookmark = bookmarkList.get(position);

        // Set video title
        holder.bookmarkTitle.setText(bookmark.getBookedTitle());

        // Load video thumbnail using Glide
        Glide.with(context)
                .load(bookmark.getBookedThumbnail())
                .placeholder(R.drawable.starbiologyvideodefaultimg)
                .error(R.drawable.starbiologyvideodefaultimg)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Log detailed error information
                        Log.e("GlideError", "Failed to load image from URL: " + bookmark.getBookedThumbnail(), e);

                        if (e != null) {
                            for (Throwable t : e.getRootCauses()) {
                                Log.e("GlideError", "Root cause: ", t);
                            }
                            e.logRootCauses("GlideError");
                        }

                        Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show();
                        return false; // Return false to allow Glide to handle setting the error placeholder
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d("GlideSuccess", "Image loaded successfully from URL: " + bookmark.getBookedThumbnail());
                        return false; // Return false to allow Glide to handle displaying the image
                    }
                })
                .into(holder.bookmarkThumbnail);


        // Set click listener to play the video
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, VideoViewActivity.class);
            intent.putExtra("videoLink", bookmark.getBookedLink());
            context.startActivity(intent);
        });

        // Set click listener for the delete button
        holder.bookmarkDeleteButton.setOnClickListener(view -> deleteBookmark(holder, bookmark));

        // Hide progress bar if not null
        if (globalProgressBar != null) {
            globalProgressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return bookmarkList.size();
    }


    private void deleteBookmark(VideoViewHolder holder, VideoBookmarkModelClass bookmark) {
        if (currentUser == null) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        CollectionReference userBookmarkRef = firestore.collection("users")
                .document(userId)
                .collection("VideoBookmarks");

        showGlobalProgressBar(); // Show the global progress bar

        // Assuming 'bookedLink' is the unique identifier for the bookmark
        userBookmarkRef.whereEqualTo("bookedLink", bookmark.getBookedLink())
                .get()
                .addOnCompleteListener(task -> {
                    hideGlobalProgressBar(); // Hide the global progress bar
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null && !snapshot.isEmpty()) {
                            for (DocumentSnapshot document : snapshot.getDocuments()) {
                                document.getReference().delete().addOnCompleteListener(deleteTask -> {
                                    if (deleteTask.isSuccessful()) {
                                        bookmarkList.remove(bookmark); // Remove from local list
                                        notifyDataSetChanged(); // Refresh adapter
                                        Toast.makeText(context, "Bookmark removed", Toast.LENGTH_SHORT).show();
                                    } else {
                                        Toast.makeText(context, "Failed to remove bookmark", Toast.LENGTH_SHORT).show();
                                    }
                                });
                            }
                        } else {
                            Toast.makeText(context, "Bookmark not found", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(context, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
        ImageView bookmarkThumbnail;
        TextView bookmarkTitle;
        ImageButton bookmarkDeleteButton;

        public VideoViewHolder(@NonNull View itemView) {
            super(itemView);
            bookmarkThumbnail = itemView.findViewById(R.id.bookmark_thumbnail);
            bookmarkTitle = itemView.findViewById(R.id.bookmark_title);
            bookmarkDeleteButton = itemView.findViewById(R.id.bookmark_delete_button);
        }
    }
}