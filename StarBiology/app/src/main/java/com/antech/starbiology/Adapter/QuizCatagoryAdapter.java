package com.antech.starbiology.Adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.antech.starbiology.Activity.QuizActivity;
import com.antech.starbiology.ModelClass.QuizModel;
import com.antech.starbiology.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;

import java.util.List;

public class QuizCatagoryAdapter extends RecyclerView.Adapter<QuizCatagoryAdapter.MyViewHolder> {
    private final Context context;
    private final List<QuizModel> quizModelList;
    private final ProgressBar progressBar;

    public QuizCatagoryAdapter(Context context, List<QuizModel> quizModelList, ProgressBar progressBar) {
        this.context = context;
        this.quizModelList = quizModelList;
        this.progressBar = progressBar;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.quiz_item_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        QuizModel quiz = quizModelList.get(position);
        holder.quizTitleText.setText(quiz.getTitle());
        holder.quizSubtitleText.setText(quiz.getSubtitle());
        holder.quizTimeText.setText(quiz.getTime() + " min");

        Glide.with(context)
                .load(quiz.getImg())
                .apply(new RequestOptions()
                        .placeholder(R.drawable.quizbackimg)
                        .error(R.drawable.quizbackimg))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {

                        return false;
                    }
                })
                .into(holder.quizImageView);

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, QuizActivity.class);
            intent.putExtra("quizModel", quizModelList.get(position));
            context.startActivity(intent);

        });



        if (progressBar != null && position == quizModelList.size() - 1) {
            progressBar.setVisibility(View.GONE);
    }
    }

    @Override
    public int getItemCount() {
        return quizModelList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        private final TextView quizTitleText;
        private final TextView quizSubtitleText;
        private final TextView quizTimeText;
        private final ImageView quizImageView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            quizTitleText = itemView.findViewById(R.id.quizTitle);
            quizTitleText.setSelected(true);
            quizSubtitleText = itemView.findViewById(R.id.quizSubject);
            quizSubtitleText.setSelected(true);
            quizTimeText = itemView.findViewById(R.id.quizTime);
            quizImageView = itemView.findViewById(R.id.quizImg);
        }
    }
}
