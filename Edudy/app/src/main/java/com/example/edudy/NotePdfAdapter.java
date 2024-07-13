package com.example.edudy;

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

public class NotePdfAdapter extends RecyclerView.Adapter<NotePdfAdapter.NotePdfViewHolder> {
    private Context context;
    private List<NotePdfModelClass> notePdfModelClassList;
    private ProgressBar progressBar;

    public NotePdfAdapter(Context context, List<NotePdfModelClass> notePdfModelClassList, ProgressBar progressBar) {
        this.context = context;
        this.notePdfModelClassList = notePdfModelClassList;
        this.progressBar = progressBar;
    }

    @NonNull
    @Override
    public NotePdfViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.pdf_list_item, parent, false);
        return new NotePdfViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotePdfViewHolder holder, int position) {
        NotePdfModelClass pdfFile = notePdfModelClassList.get(position);

        holder.subTitle.setText(pdfFile.getTopic());
        holder.subject.setText(pdfFile.getSubject());

        Glide.with(context)
                .load(pdfFile.getImg())
                .placeholder(R.drawable.noteerrorimage)
                .error(R.drawable.noteerrorimage)
                .into(holder.subImg);

        updateBookmarkIcon(holder.bookmarkButton, holder.bookmarkProgressbar, pdfFile.getUrl());

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, NotePdfViewActivity.class);
            intent.putExtra("pdfUrl", pdfFile.getUrl());
            context.startActivity(intent);
        });

        holder.bookmarkButton.setOnClickListener(view -> handleBookmarkAction(holder.bookmarkButton, holder.bookmarkProgressbar, pdfFile));

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    private void updateBookmarkIcon(ImageButton bookmarkButton, ProgressBar bookmarkProgressbar, String url) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userBookmarkRef = FirebaseDatabase.getInstance().getReference("EdudyUsers")
                .child(userId).child("NoteBookmarks");

        userBookmarkRef.orderByChild("bookedUrl").equalTo(url).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    bookmarkButton.setImageResource(R.drawable.baseline_bookmark_24);
                } else {
                    bookmarkButton.setImageResource(R.drawable.baseline_bookmark_border_24);
                }
                bookmarkProgressbar.setVisibility(View.GONE);
                bookmarkButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error checking bookmark: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                bookmarkProgressbar.setVisibility(View.GONE);
                bookmarkButton.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public int getItemCount() {
        return notePdfModelClassList.size();
    }

    private void handleBookmarkAction(ImageButton bookmarkButton, ProgressBar bookmarkProgressbar, NotePdfModelClass pdfFile) {
        bookmarkButton.setVisibility(View.GONE);
        bookmarkProgressbar.setVisibility(View.VISIBLE);

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference userBookmarkRef = FirebaseDatabase.getInstance().getReference("EdudyUsers")
                .child(userId).child("NoteBookmarks");

        userBookmarkRef.orderByChild("bookedUrl").equalTo(pdfFile.getUrl()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    // Remove existing bookmark
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                        String notebookmarkId = dataSnapshot.getKey();
                        userBookmarkRef.child(notebookmarkId).removeValue()
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(context, "Bookmark removed", Toast.LENGTH_SHORT).show();
                                    bookmarkButton.setImageResource(R.drawable.baseline_bookmark_border_24);
                                    bookmarkButton.setVisibility(View.VISIBLE);
                                    bookmarkProgressbar.setVisibility(View.GONE);
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(context, "Failed to remove bookmark: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                    bookmarkButton.setVisibility(View.VISIBLE);
                                    bookmarkProgressbar.setVisibility(View.GONE);
                                });
                    }
                } else {
                    // Add new bookmark
                    String bookmarkId = userBookmarkRef.push().getKey(); // Ensure unique key
                    Map<String, Object> bookmarkMap = new HashMap<>();
                    bookmarkMap.put("bookedImg", pdfFile.getImg());
                    bookmarkMap.put("bookedTopic", pdfFile.getTopic());
                    bookmarkMap.put("bookedUrl", pdfFile.getUrl());

                    userBookmarkRef.child(bookmarkId).setValue(bookmarkMap)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(context, "Bookmark added", Toast.LENGTH_SHORT).show();
                                bookmarkButton.setImageResource(R.drawable.baseline_bookmark_24);
                                bookmarkButton.setVisibility(View.VISIBLE);
                                bookmarkProgressbar.setVisibility(View.GONE);
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(context, "Failed to add bookmark: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                bookmarkButton.setVisibility(View.VISIBLE);
                                bookmarkProgressbar.setVisibility(View.GONE);
                            });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(context, "Error handling bookmark: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                bookmarkButton.setVisibility(View.VISIBLE);
                bookmarkProgressbar.setVisibility(View.GONE);
            }
        });
    }

    public class NotePdfViewHolder extends RecyclerView.ViewHolder {
        TextView subTitle, subject;
        ImageView subImg;
        ImageButton bookmarkButton;
        ProgressBar bookmarkProgressbar;

        public NotePdfViewHolder(@NonNull View itemView) {
            super(itemView);
            subTitle = itemView.findViewById(R.id.subTitle);
            subject = itemView.findViewById(R.id.subject);
            subImg = itemView.findViewById(R.id.subImg);
            bookmarkButton = itemView.findViewById(R.id.bookmarkNoteBtn);
            bookmarkProgressbar = itemView.findViewById(R.id.bookmarkProgressBar);
        }
    }
}