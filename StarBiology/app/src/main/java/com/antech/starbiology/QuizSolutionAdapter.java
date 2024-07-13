package com.antech.starbiology;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class QuizSolutionAdapter extends RecyclerView.Adapter<QuizSolutionAdapter.ViewHolder> {

    private final Context context;
    private final List<QuizSolutionModelClass> questionAnswers;

    public QuizSolutionAdapter(Context context, List<QuizSolutionModelClass> questionAnswers) {
        this.context = context;
        this.questionAnswers = questionAnswers;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_solution, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        QuizSolutionModelClass qa = questionAnswers.get(position);
        holder.questionText.setText(qa.getQuestion());
        holder.answerText.setText(qa.getAnswer());

    }

    @Override
    public int getItemCount() {
        return questionAnswers.size();
    }
     public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView questionText;
        TextView answerText;

      public ViewHolder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.question_text);
            answerText = itemView.findViewById(R.id.answer_text);
        }
    }
}
