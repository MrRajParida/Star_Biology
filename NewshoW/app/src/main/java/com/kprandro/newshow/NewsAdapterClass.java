package com.kprandro.newshow;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class NewsAdapterClass extends RecyclerView.Adapter<NewsAdapterClass.NewsViewHolder> {
    private Context context;
    private List<NewsItemClass> newsItemClass;
    private SharedViewModel sharedViewModel;

    public NewsAdapterClass(Context context, List<NewsItemClass> newsItemClass, SharedViewModel sharedViewModel) {
        this.context = context;
        this.newsItemClass = newsItemClass;
        this.sharedViewModel = sharedViewModel;
    }

    @NonNull
    @Override
    public NewsAdapterClass.NewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.news_layout, parent, false);
        return new NewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NewsAdapterClass.NewsViewHolder holder, int position) {
        NewsItemClass newsItem = newsItemClass.get(position);

        holder.userImg.setImageResource(newsItem.getUserImg());
        holder.userId.setText(newsItem.getUserId());
        holder.newsImg.setImageResource(newsItem.getNewsImg());
        holder.newsTitle.setText(newsItem.getNewsTitle());

        String fullDescription = newsItem.getNewsDescription();
        holder.newsDescription.setText(fullDescription);

        // Measure text to see if it exceeds 7 lines
        holder.newsDescription.post(() -> {
            int lineCount = holder.newsDescription.getLineCount();
            if (lineCount > 7) {
                holder.newsDescription.setMaxLines(7);
                holder.readMore.setText("Read More");
            } else {
                holder.readMore.setVisibility(View.GONE);
            }
        });

        holder.newsDescription.setOnClickListener(v -> {
            // Intent to navigate to FullNewsActivity
            Intent intent = new Intent(context, FullNewsActivity.class);
            intent.putExtra("newsUserImg", newsItem.getUserImg());
            intent.putExtra("newsUserId", newsItem.getUserId());
            intent.putExtra("newsUserVerified", newsItem.getVerified());
            intent.putExtra("newsImg", newsItem.getNewsImg());
            intent.putExtra("newsTitle", newsItem.getNewsTitle());
            intent.putExtra("newsDescription", newsItem.getNewsDescription());
            context.startActivity(intent);
        });

        // Show verified or not
        if (newsItem.getVerified()) {
            holder.userVerified.setVisibility(View.VISIBLE);
        } else {
            holder.userVerified.setVisibility(View.GONE);
        }
    }


    @Override
    public int getItemCount() {
        return newsItemClass.size();
    }

    public class NewsViewHolder extends RecyclerView.ViewHolder {
        CircleImageView userImg;
        TextView userId, userFollow, newsTitle, newsDescription, readMore;
        ImageView userVerified, newsImg;
        ImageButton newsShare, newsBookmark;

        public NewsViewHolder(@NonNull View itemView) {
            super(itemView);
            userImg = itemView.findViewById(R.id.userImage);
            userId = itemView.findViewById(R.id.userId);
            userVerified = itemView.findViewById(R.id.userVerified);
            userFollow = itemView.findViewById(R.id.userFollow);
            newsImg = itemView.findViewById(R.id.newsImg);
            newsTitle = itemView.findViewById(R.id.newsTitle);
            newsDescription = itemView.findViewById(R.id.newsDescription);
            newsShare = itemView.findViewById(R.id.newsShare);
            newsBookmark = itemView.findViewById(R.id.newsBookmark);
            readMore = itemView.findViewById(R.id.readMore);
        }
    }
}
