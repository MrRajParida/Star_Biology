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
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.antech.starbiology.Activity.NoteViewActivity;
import com.antech.starbiology.ModelClass.QuestionModelClass;
import com.antech.starbiology.R;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuestionListAdapter extends RecyclerView.Adapter<QuestionListAdapter.QuestionListViewHolder> {

    private final Context context;
    private final List<QuestionModelClass> questionList;
    private final ProgressBar progressBar;
    private final FirebaseFirestore firestore;
    private final FirebaseUser currentUser;

    public QuestionListAdapter(Context context, List<QuestionModelClass> questionList, ProgressBar progressBar) {
        this.context = context;
        this.questionList = questionList;
        this.progressBar = progressBar;
        this.firestore = FirebaseFirestore.getInstance();
        this.currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    @NonNull
    @Override
    public QuestionListViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.question_item_list, parent, false);
        return new QuestionListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuestionListViewHolder holder, int position) {
        QuestionModelClass question = questionList.get(position);

        // Set question title and subject
        holder.quesTitle.setText(question.getqTopic());
        holder.quesSubject.setText(question.getqSubject());

        // Load question image using Glide
        Glide.with(context)
                .load(question.getqImg())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.starbiologyquestiondefaultimg)
                        .error(R.drawable.starbiologyquestiondefaultimg))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        // Log the error with details
                        Log.e("GlideError", "Failed to load image from URL: " + question.getqImg(), e);

                        // Optionally display a user-friendly message
                        Toast.makeText(context, "Failed to load image", Toast.LENGTH_SHORT).show();

                        // Return false to allow Glide to handle setting the error placeholder
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        // Log successful loading
                        Log.d("GlideSuccess", "Image loaded successfully from URL: " + question.getqImg());

                        // Return false to allow Glide to handle displaying the image
                        return false;
                    }
                })
                .into(holder.quesImg);


        // Update bookmark icon
        updateBookmarkIcon(holder.quesBookmarkBtn, question.getqUrl());

        // Set click listener for item view
        holder.itemView.setOnClickListener(view -> openNoteViewActivity(question.getqUrl()));

        // Set click listener for bookmark button
        holder.quesBookmarkBtn.setOnClickListener(view -> handleBookmarkAction(holder.quesBookmarkBtn, question));

        // Hide the progress bar after loading the last item
        if (progressBar != null && position == questionList.size() - 1) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void openNoteViewActivity(String pdfUrl) {
        Intent intent = new Intent(context, NoteViewActivity.class);
        intent.putExtra("pdfUrl", pdfUrl);
        context.startActivity(intent);
    }

    @Override
    public int getItemCount() {
        return questionList.size();
    }

    private void updateBookmarkIcon(ImageButton bookmarkButton, String url) {
        if (currentUser == null) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        CollectionReference userBookmarksRef = firestore.collection("users")
                .document(userId)
                .collection("QuestionBookmarks");

        // Show progress bar when checking bookmark state
        progressBar.setVisibility(View.VISIBLE);
        bookmarkButton.setVisibility(View.GONE);

        userBookmarksRef.whereEqualTo("qBookedUrl", url)
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

    private void handleBookmarkAction(ImageButton bookmarkButton, QuestionModelClass question) {
        if (currentUser == null) {
            Toast.makeText(context, "User not authenticated", Toast.LENGTH_SHORT).show();
            return;
        }

        String userId = currentUser.getUid();
        CollectionReference userBookmarksRef = firestore.collection("users")
                .document(userId)
                .collection("QuestionBookmarks");

        bookmarkButton.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

        // Check if the question is already bookmarked
        userBookmarksRef.whereEqualTo("qBookedUrl", question.getqUrl())
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
                            bookmarkMap.put("qBookedImg", question.getqImg());
                            bookmarkMap.put("qBookedTopic", question.getqTopic());
                            bookmarkMap.put("qBookedSubject", question.getqSubject());
                            bookmarkMap.put("qBookedUrl", question.getqUrl());

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

    public static class QuestionListViewHolder extends RecyclerView.ViewHolder {
        ImageView quesImg;
        TextView quesTitle, quesSubject;
        ImageButton quesBookmarkBtn;

        public QuestionListViewHolder(@NonNull View itemView) {
            super(itemView);
            quesImg = itemView.findViewById(R.id.quesListImg);
            quesTitle = itemView.findViewById(R.id.quesListTitle);
            quesSubject = itemView.findViewById(R.id.quesListSubject);
            quesSubject.setSelected(true);
            quesBookmarkBtn = itemView.findViewById(R.id.quesListBookmarkBtn);
        }
    }
}