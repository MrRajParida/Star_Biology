package com.antech.starbiology.Activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.antech.starbiology.ModelClass.QuizModel;
import com.antech.starbiology.QuizSolutionActivity;
import com.antech.starbiology.QuizSolutionModelClass;
import com.antech.starbiology.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int SHARE_REQUEST_CODE = 1;

    public static List<QuizModel.QuestionModel> questionModelList;
    public static String time = "";

    private long timeLeftInMillis;
    private boolean isTimerRunning = false;

    private TextView questionIndicatorTextView;
    private TextView timerIndicatorTextView;
    private TextView questionTextView;
    private ProgressBar questionProgressIndicator;
    private Button btn0, btn1, btn2, btn3, nextBtn;

    private int currentQuestionIndex = 0;
    private String selectedAnswer = "";
    private int score = 0;

    private ImageButton bookmark, share, pause;

    private CountDownTimer countDownTimer;
    private AlertDialog dialog;

    private List<QuizSolutionModelClass> questionAnswersList = new ArrayList<>();
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);

        String appName = getString(R.string.app_name);
        String packageName = this.getApplicationContext().getPackageName();
        String appLink = "https://play.google.com/store/apps/details?id=" + packageName;

        QuizModel quizModel = getIntent().getParcelableExtra("quizModel");
        if (quizModel != null) {
            questionModelList = quizModel.getQuestionList();
            time = quizModel.getTime();
        } else {
            Toast.makeText(this, "Quiz data not found", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }


        QuizSolutionModelClass answer = new QuizSolutionModelClass(questionModelList.get(currentQuestionIndex).getQuestion(), selectedAnswer);
        questionAnswersList.add(answer);

        progressBar = findViewById(R.id.loadBookmark);
        progressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.app_green),
                android.graphics.PorterDuff.Mode.SRC_IN);
        questionIndicatorTextView = findViewById(R.id.question_indicator_textview);
        timerIndicatorTextView = findViewById(R.id.timer_indicator_textview);
        questionTextView = findViewById(R.id.question_textview);
        questionProgressIndicator = findViewById(R.id.question_progress_indicator);
        bookmark = findViewById(R.id.bookmarkBtn);
        share = findViewById(R.id.shareBtn);
        pause = findViewById(R.id.pauseBtn);

        btn0 = findViewById(R.id.btn0);
        btn1 = findViewById(R.id.btn1);
        btn2 = findViewById(R.id.btn2);
        btn3 = findViewById(R.id.btn3);
        nextBtn = findViewById(R.id.next_btn);

        btn0.setOnClickListener(this);
        btn1.setOnClickListener(this);
        btn2.setOnClickListener(this);
        btn3.setOnClickListener(this);
        nextBtn.setOnClickListener(this);

        loadQuestions();
        startTimer(Long.parseLong(time) * 60 * 1000L);

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pauseTimer();
                pause.setImageResource(R.drawable.baseline_play_circle_24);
                AlertDialog.Builder builder = new AlertDialog.Builder(QuizActivity.this);

                builder.setTitle("Quiz Paused")
                        .setMessage("What would you like to do?")
                        .setPositiveButton("Resume", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                pause.setImageResource(R.drawable.baseline_pause_circle_outline_24);
                                resumeTimer();
                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("Exit Quiz", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        bookmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

                String currentQuestion = getCurrentQuestion();
                String correctAnswer = getCorrectAnswer();

                FirebaseFirestore db = FirebaseFirestore.getInstance();
                CollectionReference bookmarksRef = db.collection("users").document(userId).collection("QuizBookmarked");

                progressBar.setVisibility(View.VISIBLE);

                bookmarksRef.whereEqualTo("question", currentQuestion)
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                progressBar.setVisibility(View.GONE);
                                if (task.isSuccessful() && !task.getResult().isEmpty()) {
                                    // Bookmark exists, so remove it
                                    for (DocumentSnapshot document : task.getResult()) {
                                        bookmarksRef.document(document.getId()).delete()
                                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                    @Override
                                                    public void onSuccess(Void aVoid) {
                                                        Toast.makeText(QuizActivity.this, "Bookmark removed!", Toast.LENGTH_SHORT).show();
                                                        bookmark.setImageResource(R.drawable.baseline_bookmark_border_24);
                                                    }
                                                })
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        Toast.makeText(QuizActivity.this, "Error removing bookmark!", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                    }
                                } else {
                                    Map<String, Object> bookmarkData = new HashMap<>();
                                    bookmarkData.put("question", currentQuestion);
                                    bookmarkData.put("correctAnswer", correctAnswer);
                                    bookmarkData.put("timestamp", FieldValue.serverTimestamp());

                                    bookmarksRef.add(bookmarkData)
                                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                                @Override
                                                public void onSuccess(DocumentReference documentReference) {
                                                    Toast.makeText(QuizActivity.this, "Bookmark added!", Toast.LENGTH_SHORT).show();
                                                    // Update the bookmark icon to bookmarked
                                                    bookmark.setImageResource(R.drawable.baseline_bookmark_24); // Use your bookmarked icon
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(QuizActivity.this, "Error adding bookmark!", Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        });
            }

            private String getCurrentQuestion() {
                return questionModelList.get(currentQuestionIndex).getQuestion();
            }

            private String getCorrectAnswer() {
                return questionModelList.get(currentQuestionIndex).getCorrect();
            }
        });

        share.setOnClickListener(view -> {
            pauseTimer();
            QuizModel.QuestionModel currentQuestion = questionModelList.get(currentQuestionIndex);
            String questionText = currentQuestion.getQuestion();
            List<String> options = currentQuestion.getOptions();

            String shareBody = "*Question:* " + questionText + "\n\n*Options:*\n";
            for (int i = 0; i < options.size(); i++) {
                shareBody += (i + 1) + ". " + options.get(i) + "\n";
            }

            Intent sharingIntent = new Intent(Intent.ACTION_SEND);
            sharingIntent.setType("text/plain");
            sharingIntent.putExtra(Intent.EXTRA_SUBJECT, "Quiz Question");
            sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody + "\nCheck out this " + appName + " app on Google Play Store: " + appLink);
            startActivityForResult(Intent.createChooser(sharingIntent, "Share via"), SHARE_REQUEST_CODE);
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SHARE_REQUEST_CODE) {
            resumeTimer();
        }
    }

    private void startTimer(long totalTimeInMillis) {
        timeLeftInMillis = totalTimeInMillis;
        countDownTimer = new CountDownTimer(timeLeftInMillis, 1000L) {
            @Override
            public void onTick(long millisUntilFinished) {
                timeLeftInMillis = millisUntilFinished;
                long seconds = millisUntilFinished / 1000;
                long minutes = seconds / 60;
                long remainingSeconds = seconds % 60;
                timerIndicatorTextView.setText(String.format("%02d:%02d", minutes, remainingSeconds));
            }

            @Override
            public void onFinish() {
                isTimerRunning = false;
                finishQuiz();
            }
        }.start();
        isTimerRunning = true;
    }

    private void pauseTimer() {
        if (countDownTimer != null && isTimerRunning) {
            countDownTimer.cancel();
            isTimerRunning = false;
        }
    }


    private void resumeTimer() {
        if (!isTimerRunning) {
            startTimer(timeLeftInMillis);
        }
    }

    private void loadQuestions() {
        selectedAnswer = "";
        if (currentQuestionIndex == questionModelList.size()) {
            finishQuiz();
            return;
        }

        questionIndicatorTextView.setText("Question " + (currentQuestionIndex + 1) + "/" + questionModelList.size());
        questionProgressIndicator.setProgress((int) ((currentQuestionIndex / (float) questionModelList.size()) * 100));
        questionTextView.setText(questionModelList.get(currentQuestionIndex).getQuestion());
        btn0.setText(questionModelList.get(currentQuestionIndex).getOptions().get(0));
        btn1.setText(questionModelList.get(currentQuestionIndex).getOptions().get(1));
        btn2.setText(questionModelList.get(currentQuestionIndex).getOptions().get(2));
        btn3.setText(questionModelList.get(currentQuestionIndex).getOptions().get(3));

        checkIfCurrentQuestionIsBookmarked();
    }

    private void checkIfCurrentQuestionIsBookmarked() {
        progressBar.setVisibility(View.VISIBLE);
        pauseTimer();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        String currentQuestion = questionModelList.get(currentQuestionIndex).getQuestion();

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        CollectionReference bookmarksRef = db.collection("users").document(userId).collection("QuizBookmarked");

        bookmarksRef.whereEqualTo("question", currentQuestion)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressBar.setVisibility(View.GONE);
                        resumeTimer();
                        if (task.isSuccessful() && !task.getResult().isEmpty()) {
                            questionModelList.get(currentQuestionIndex).setBookmarked(true);
                            bookmark.setImageResource(R.drawable.baseline_bookmark_24); // Set to bookmarked icon
                        } else {
                            questionModelList.get(currentQuestionIndex).setBookmarked(false);
                            bookmark.setImageResource(R.drawable.baseline_bookmark_border_24); // Set to unbookmarked icon
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressBar.setVisibility(View.GONE);
                        resumeTimer();
                    }
                });
    }

    @Override
    public void onClick(View view) {
        btn0.setBackgroundColor(getResources().getColor(com.denzcoskun.imageslider.R.color.grey_font));
        btn1.setBackgroundColor(getResources().getColor(com.denzcoskun.imageslider.R.color.grey_font));
        btn2.setBackgroundColor(getResources().getColor(com.denzcoskun.imageslider.R.color.grey_font));
        btn3.setBackgroundColor(getResources().getColor(com.denzcoskun.imageslider.R.color.grey_font));

        Button clickedBtn = (Button) view;
        if (clickedBtn.getId() == R.id.next_btn) {
            if (selectedAnswer.isEmpty()) {
                Snackbar.make(view, "Please select an answer to continue", Snackbar.LENGTH_SHORT).show();
                return;
            }

            if (selectedAnswer.equals(questionModelList.get(currentQuestionIndex).getCorrect())) {
                score++;
            }

            currentQuestionIndex++;
            loadQuestions();
        } else {
            selectedAnswer = clickedBtn.getText().toString();
            clickedBtn.setBackgroundColor(getResources().getColor(R.color.app_yellow));
        }
    }

    private void finishQuiz() {
        if (isFinishing() || isDestroyed()) return;

        int totalQuestions = questionModelList.size();
        int percentage = (int) (((float) score / totalQuestions) * 100);

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View dialogView = getLayoutInflater().inflate(R.layout.score_dialog, null);

        TextView scoreProgressText = dialogView.findViewById(R.id.score_progress_text);
        ProgressBar scoreProgressIndicator = dialogView.findViewById(R.id.score_progress_indicator);
        TextView scoreTitle = dialogView.findViewById(R.id.score_title);
        TextView scoreSubtitle = dialogView.findViewById(R.id.score_subtitle);
        Button finishBtn = dialogView.findViewById(R.id.finish_btn);

        scoreProgressIndicator.setProgress(percentage);
        scoreProgressText.setText(percentage + " %");

        if (percentage > 60) {
            scoreTitle.setText("Congrats! You have passed");
            scoreTitle.setTextColor(Color.BLUE);
        } else {
            scoreTitle.setText("Oops! You have failed");
            scoreTitle.setTextColor(Color.RED);
        }

        scoreSubtitle.setText(score + " out of " + totalQuestions + " are correct");

        finishBtn.setOnClickListener(v -> {
            if (!isFinishing() && !isDestroyed()) {
                finish();
            }
        });

        new android.os.Handler().postDelayed(() -> {
            if (!isFinishing() && !isDestroyed()) {
                dialog = builder.setView(dialogView)
                        .setCancelable(false)
                        .create();  // Create the dialog here
                dialog.show();  // Show the dialog
            }
        }, 500);
    }

    @Override
    protected void onDestroy() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }

        if (countDownTimer != null) {
            countDownTimer.cancel();
            countDownTimer = null;
        }
        super.onDestroy();
    }
}
