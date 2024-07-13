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
import com.starproduction.starbiology.ModelClass.QuestionBookmarkModelClass;
import com.starproduction.starbiology.R;

import java.util.List;

public class QuestionBookmarkAdapter extends RecyclerView.Adapter<QuestionBookmarkAdapter.QuestionBookmarkViewHolder> {
    private Context context;
    private List<QuestionBookmarkModelClass> questionBookmarkModelClassList;
    private ProgressBar progressBar; // ProgressBar for loading
    private FirebaseFirestore firestore;

    public QuestionBookmarkAdapter(Context context, List<QuestionBookmarkModelClass> questionBookmarkModelClassList, ProgressBar progressBar) {
        this.context = context;
        this.questionBookmarkModelClassList = questionBookmarkModelClassList;
        this.progressBar = progressBar;
        this.firestore = FirebaseFirestore.getInstance(); // Initialize Firestore
    }

    @NonNull
    @Override
    public QuestionBookmarkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.questionbookmark_item_list, parent, false);
        return new QuestionBookmarkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionBookmarkViewHolder holder, int position) {
        QuestionBookmarkModelClass bookmarkItem = questionBookmarkModelClassList.get(position);
        holder.subTitle.setText(bookmarkItem.getqBookedTopic());
        holder.subDesc.setText(bookmarkItem.getqBookedSubject());

        Glide.with(context)
                .load(bookmarkItem.getqBookedImg())
                .placeholder(R.drawable.starbiologyquestiondefaultimg)
                .error(R.drawable.starbiologyquestiondefaultimg)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Log detailed error information
                        Log.e("GlideError", "Failed to load image for URL: " + bookmarkItem.getqBookedImg(), e);

                        // Display a toast message to the user
                        Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show();

                        // Return false to allow Glide to handle the error image setting
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // Log successful image load
                        Log.d("GlideSuccess", "Image loaded successfully for URL: " + bookmarkItem.getqBookedImg());

                        // Return false to let Glide display the image
                        return false;
                    }
                })
                .into(holder.subImg);

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, NoteViewActivity.class);
            intent.putExtra("pdfUrl", bookmarkItem.getqBookedUrl());
            context.startActivity(intent);
        });

        holder.deleteBookmarkButton.setOnClickListener(view -> {
            deleteBookmark(holder, bookmarkItem);
        });

        if (progressBar != null && position == questionBookmarkModelClassList.size() - 1) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return questionBookmarkModelClassList.size();
    }

    public void loadBookmarks() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference userBookmarkRef = firestore.collection("users").document(userId).collection("QuestionBookmarks");

        showProgressBar(); // Show the progress bar

        // Fetch bookmarks from Firestore
        userBookmarkRef.get().addOnCompleteListener(task -> {
            hideProgressBar(); // Hide the progress bar
            if (task.isSuccessful()) {
                questionBookmarkModelClassList.clear();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    QuestionBookmarkModelClass bookmark = document.toObject(QuestionBookmarkModelClass.class);
                    questionBookmarkModelClassList.add(bookmark);
                }
                notifyDataSetChanged();
            } else {
                Toast.makeText(context, "Error loading bookmarks: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteBookmark(QuestionBookmarkViewHolder holder, QuestionBookmarkModelClass bookmarkItem) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        CollectionReference userBookmarkRef = firestore.collection("users").document(userId).collection("QuestionBookmarks");

        showProgressBar(); // Show the progress bar

        // Query to find the bookmark and delete it
        userBookmarkRef.whereEqualTo("qBookedUrl", bookmarkItem.getqBookedUrl()).get().addOnCompleteListener(task -> {
            if (task.isSuccessful() && !task.getResult().isEmpty()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    DocumentReference docRef = document.getReference();
                    docRef.delete().addOnCompleteListener(deleteTask -> {
                        hideProgressBar(); // Hide the progress bar
                        if (deleteTask.isSuccessful()) {
                            questionBookmarkModelClassList.remove(bookmarkItem); // Remove from local list
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

    public static class QuestionBookmarkViewHolder extends RecyclerView.ViewHolder {
        TextView subTitle, subDesc;
        ImageView subImg;
        ImageButton deleteBookmarkButton;

        public QuestionBookmarkViewHolder(@NonNull View itemView) {
            super(itemView);
            subTitle = itemView.findViewById(R.id.bookedQuesTitle);
            subDesc = itemView.findViewById(R.id.bookedQuesSubject);
            subImg = itemView.findViewById(R.id.bookedQuesImg);
            deleteBookmarkButton = itemView.findViewById(R.id.quesBookmarkDeleteBtn);
        }
    }
}
