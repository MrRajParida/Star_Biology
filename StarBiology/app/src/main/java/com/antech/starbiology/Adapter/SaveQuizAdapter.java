package com.antech.starbiology.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.antech.starbiology.R;
import com.antech.starbiology.ModelClass.SaveQuizModelClass;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class SaveQuizAdapter extends RecyclerView.Adapter<SaveQuizAdapter.QuizViewHolder> {

    private List<SaveQuizModelClass> quizList;
    private Context context;

    public SaveQuizAdapter(List<SaveQuizModelClass> quizList, Context context) {
        this.quizList = quizList;
        this.context = context;
    }

    @NonNull
    @Override
    public QuizViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.saved_quiz_list, parent, false);
        return new QuizViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull QuizViewHolder holder, int position) {
        SaveQuizModelClass quiz = quizList.get(position);
        holder.quizQuestion.setText(quiz.getQuestion());
        holder.quizAnswer.setText(quiz.getCorrectAnswer());

        holder.quizDeleteBtn.setOnClickListener(v -> {
            String documentId = quiz.getDocumentId();
            String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

            // Check if user and document data is valid
            if (uid == null || documentId == null) {
                Toast.makeText(context, "Error: User or quiz data is invalid", Toast.LENGTH_SHORT).show();
                return;
            }

            SaveQuizModelClass quizToRemove = quizList.get(position);

            // Remove the item from the list immediately and notify adapter
            quizList.remove(position);
            notifyItemRemoved(position);
            notifyItemRangeChanged(position, quizList.size());

            // Perform the deletion in the background
            FirebaseFirestore.getInstance().collection("users").document(uid)
                    .collection("QuizBookmarked").document(documentId)
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        // Successfully deleted from Firebase
                        Toast.makeText(context, "Quiz deleted successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        // Deletion failed, re-add the item back to the list and notify adapter
                        quizList.add(position, quizToRemove);  // Re-add the item at the same position
                        notifyItemInserted(position);
                        notifyItemRangeChanged(position, quizList.size());  // Refresh the list

                        Toast.makeText(context, "Error deleting quiz: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    });
        });




    }

    @Override
    public int getItemCount() {
        return quizList.size();
    }

    public static class QuizViewHolder extends RecyclerView.ViewHolder {

        TextView quizQuestion, quizAnswer;
        ImageButton quizDeleteBtn;

        public QuizViewHolder(@NonNull View itemView) {
            super(itemView);
            quizQuestion = itemView.findViewById(R.id.quizQuestion);
            quizAnswer = itemView.findViewById(R.id.quizAnswer);
            quizDeleteBtn = itemView.findViewById(R.id.quizDeleteBtn);
        }
    }
}
