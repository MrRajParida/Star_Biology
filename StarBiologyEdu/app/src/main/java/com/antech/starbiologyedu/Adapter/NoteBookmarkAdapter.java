package com.antech.starbiologyedu.Adapter;

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
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.starproduction.starbiology.Activity.NoteViewActivity;
import com.starproduction.starbiology.ModelClass.NoteBookmarkModelClass;
import com.starproduction.starbiology.R;

import java.util.List;

public class NoteBookmarkAdapter extends RecyclerView.Adapter<NoteBookmarkAdapter.NoteBookmarkViewHolder> {
    private Context context;
    private List<NoteBookmarkModelClass> noteBookmarkModelClassList;
    private ProgressBar progressBar; // ProgressBar for loading
    private FirebaseFirestore firestore;

    public NoteBookmarkAdapter(Context context, List<NoteBookmarkModelClass> noteBookmarkModelClassList, ProgressBar progressBar) {
        this.context = context;
        this.noteBookmarkModelClassList = noteBookmarkModelClassList;
        this.progressBar = progressBar;
        this.firestore = FirebaseFirestore.getInstance(); // Initialize Firestore
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
                .placeholder(R.drawable.starbiologynotedefaultimg)
                .error(R.drawable.starbiologynotedefaultimg)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Log the error details for debugging
                        Log.e("GlideError", "Failed to load image for URL: " + bookmarkItem.getBookedImg(), e);

                        // Display a toast message to the user
                        Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show();

                        // Return false to let Glide handle the error image setting
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // Log success for debugging purposes
                        Log.d("GlideSuccess", "Image loaded successfully for URL: " + bookmarkItem.getBookedImg());

                        // Return false to let Glide handle displaying the image
                        return false;
                    }
                })
                .into(holder.subImg);


        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, NoteViewActivity.class);
            intent.putExtra("pdfUrl", bookmarkItem.getBookedUrl());
            context.startActivity(intent);
        });

        holder.deleteBookmarkButton.setOnClickListener(view -> {
            deleteBookmark(holder, bookmarkItem);
        });
    }

    @Override
    public int getItemCount() {
        return noteBookmarkModelClassList.size();
    }

    public void loadBookmarks() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference userBookmarkRef = firestore.collection("users").document(userId).collection("NoteBookmarks");

        showProgressBar(); // Show the progress bar

        // Fetch bookmarks from Firestore
        userBookmarkRef.get().addOnCompleteListener(task -> {
            hideProgressBar(); // Hide the progress bar
            if (task.isSuccessful()) {
                noteBookmarkModelClassList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    NoteBookmarkModelClass bookmark = document.toObject(NoteBookmarkModelClass.class);
                    noteBookmarkModelClassList.add(bookmark);
                }
                notifyDataSetChanged();
            } else {
                Toast.makeText(context, "Error loading bookmarks: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteBookmark(NoteBookmarkViewHolder holder, NoteBookmarkModelClass bookmarkItem) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference userBookmarkRef = firestore.collection("users").document(userId).collection("NoteBookmarks");

        showProgressBar(); // Show the progress bar

        // Query to find the bookmark and delete it
        userBookmarkRef.whereEqualTo("bookedUrl", bookmarkItem.getBookedUrl()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    DocumentReference docRef = document.getReference();
                    docRef.delete().addOnCompleteListener(deleteTask -> {
                        hideProgressBar(); // Hide the progress bar
                        if (deleteTask.isSuccessful()) {
                            noteBookmarkModelClassList.remove(bookmarkItem); // Remove from local list
                            notifyDataSetChanged(); // Refresh adapter
                            Toast.makeText(context, "Bookmark removed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(context, "Failed to remove bookmark", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } else {
                hideProgressBar(); // Hide the progress bar
                Toast.makeText(context, "Bookmark not found", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            hideProgressBar(); // Hide the progress bar
            Toast.makeText(context, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }

    private void showProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.VISIBLE);
        }
    }

    private void hideProgressBar() {
        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
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