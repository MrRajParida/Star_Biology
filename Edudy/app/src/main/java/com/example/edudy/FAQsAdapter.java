package com.example.edudy;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FAQsAdapter extends RecyclerView.Adapter<FAQsAdapter.FAQsViewHOlder> {
    private List<FAQItem> faqItemList;
    private Context context;
    private ProgressBar progressBar;

    public FAQsAdapter(List<FAQItem> faqItemList, Context context, ProgressBar progressBar) {
        this.faqItemList = faqItemList;
        this.context = context;
        this.progressBar = progressBar;
    }

    @NonNull
    @Override
    public FAQsAdapter.FAQsViewHOlder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_faqs, parent, false);
        return new FAQsViewHOlder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FAQsAdapter.FAQsViewHOlder holder, int position) {
        FAQItem faqItem = faqItemList.get(position);
        holder.questionText.setText(faqItem.getQuestion());
        holder.answerText.setText(faqItem.getAnswer());

        if (progressBar != null) {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return faqItemList.size();
    }

    public class FAQsViewHOlder extends RecyclerView.ViewHolder {
        TextView questionText, answerText;
        public FAQsViewHOlder(@NonNull View itemView) {
            super(itemView);
            questionText = itemView.findViewById(R.id.faqsQuestion);
            answerText = itemView.findViewById(R.id.faqsAnswer);
        }
    }
}
