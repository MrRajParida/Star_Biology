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
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.starproduction.starbiology.Activity.NoteViewActivity;
import com.starproduction.starbiology.ModelClass.NoteModelClass;
import com.starproduction.starbiology.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class NoteAdapter extends RecyclerView.Adapter<NoteAdapter.NoteViewHolder> {

    private final Context context;
    private final List<NoteModelClass> noteModelClassList;
    private final ProgressBar progressBar;  // Single ProgressBar for both loading and bookmarking
    private final FirebaseFirestore firestore;
    private final FirebaseUser currentUser;

    public NoteAdapter(Context context, List<NoteModelClass> noteModelClassList, ProgressBar progressBar) {
        this.context = context;
        this.noteModelClassList = noteModelClassList;
        this.progressBar = progressBar;
        this.firestore = FirebaseFirestore.getInstance();
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.note_item, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        NoteModelClass note = noteModelClassList.get(position);

        holder.subTitle.setText(note.getTopic());
        holder.subject.setText(note.getSubject());

        // Load image using Glide with error handlin
        Glide.with(context)
                .load(note.getImg())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.starbiologynotedefaultimg)
                        .error(R.drawable.starbiologynotedefaultimg))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Log the error
                        Log.e("GlideError", "Image load failed for URL: " + note.getImg(), e);
                        // Optionally, show a toast message
                        Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show();
                        return false; // Return false to allow the error placeholder to be set
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // Image successfully loaded, you can log here if needed
                        Log.d("GlideSuccess", "Image loaded successfully for URL: " + note.getImg());
                        return false; // Return false to allow Glide to handle the view
                    }
                })
                .into(holder.subImg);

        // Update bookmark icon based on existing bookmark state
        updateBookmarkIcon(holder.bookmarkButton, note.getUrl());

        holder.itemView.setOnClickListener(view -> openNoteViewActivity(note.getUrl()));

        holder.bookmarkButton.setOnClickListener(view -> handleBookmarkAction(holder.bookmarkButton, note));


        // Hide the progress bar after loading the notes
        if (progressBar != null && position == noteModelClassList.size() - 1) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return noteModelClassList.size();
    }

    private void openNoteViewActivity(String pdfUrl) {
        Intent intent = new Intent(context, NoteViewActivity.class);
        intent.putExtra("pdfUrl", pdfUrl);
        context.startActivity(intent);
    }

    private void updateBookmarkIcon(ImageButton bookmarkButton, String url) {
        if (currentUser == null) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        CollectionReference userBookmarksRef = firestore.collection("users")
                .document(userId)
                .collection("NoteBookmarks");

        // Show progress bar when checking bookmark state
        progressBar.setVisibility(View.VISIBLE);
        bookmarkButton.setVisibility(View.GONE);

        // Query Firestore to check if the bookmark already exists
        userBookmarksRef.whereEqualTo("bookedUrl", url)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(View.GONE);
                    bookmarkButton.setVisibility(View.VISIBLE);

                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null && !snapshot.isEmpty()) {
                            // Bookmark exists, set the icon to filled
                            bookmarkButton.setImageResource(R.drawable.baseline_bookmark_24);
                        } else {
                            // Bookmark doesn't exist, set the icon to outline
                            bookmarkButton.setImageResource(R.drawable.baseline_bookmark_border_24);
                        }
                    } else {
                        Toast.makeText(context, "Error checking bookmark: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void handleBookmarkAction(ImageButton bookmarkButton, NoteModelClass note) {
        if (currentUser == null) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        CollectionReference userBookmarksRef = firestore.collection("users")
                .document(userId)
                .collection("NoteBookmarks");

        bookmarkButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        // Check if the note is already bookmarked
        userBookmarksRef.whereEqualTo("bookedUrl", note.getUrl())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        QuerySnapshot snapshot = task.getResult();
                        if (snapshot != null && !snapshot.isEmpty()) {
                            // Remove existing bookmark
                            for (DocumentSnapshot document : snapshot.getDocuments()) {
                                userBookmarksRef.document(document.getId())
                                        .delete()
                                        .addOnSuccessListener(aVoid -> {
                                            Toast.makeText(context, "Bookmark removed", Toast.LENGTH_SHORT).show();
                                            bookmarkButton.setImageResource(R.drawable.baseline_bookmark_border_24);
                                            bookmarkButton.setVisibility(View.VISIBLE);
                                            progressBar.setVisibility(View.GONE);
                                        })
                                        .addOnFailureListener(e -> {
                                            Toast.makeText(context, "Failed to remove bookmark: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                            bookmarkButton.setVisibility(View.VISIBLE);
                                            progressBar.setVisibility(View.GONE);
                                        });
                            }
                        } else {
                            // Add new bookmark
                            Map<String, Object> bookmarkMap = new HashMap<>();
                            bookmarkMap.put("bookedImg", note.getImg());
                            bookmarkMap.put("bookedTopic", note.getTopic());
                            bookmarkMap.put("bookedUrl", note.getUrl());

                            userBookmarksRef.add(bookmarkMap)
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Bookmark added", Toast.LENGTH_SHORT).show();
                                        bookmarkButton.setImageResource(R.drawable.baseline_bookmark_24);
                                        bookmarkButton.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                    })
                                    .addOnFailureListener(e -> {
                                        Toast.makeText(context, "Failed to add bookmark: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                        bookmarkButton.setVisibility(View.VISIBLE);
                                        progressBar.setVisibility(View.GONE);
                                    });
                        }
                    } else {
                        Toast.makeText(context, "Error handling bookmark: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        bookmarkButton.setVisibility(View.VISIBLE);
                        progressBar.setVisibility(View.GONE);
                    }
                });
    }

    public static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView subTitle, subject;
        ImageView subImg;
        ImageButton bookmarkButton;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            subTitle = itemView.findViewById(R.id.subTitle);
            subject = itemView.findViewById(R.id.subject);
            subImg = itemView.findViewById(R.id.subImg);
            bookmarkButton = itemView.findViewById(R.id.bookmarkNoteBtn);
        }
    }
}