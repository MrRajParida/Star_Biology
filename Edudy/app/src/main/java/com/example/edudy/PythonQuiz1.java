package com.example.edudy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PythonQuiz1 extends AppCompatActivity {
    private TextView question, numberofquestion;
    private FloatingActionButton Bookmarkbtn;
    private LinearLayout OpctionsContainer;
    private Button sharebtn, nexrbutton;
    private int count = 0;
    private List<QuestionsAdapter> list;
    private int position = 0;
    private int score = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_python_quiz1);

        this.setTitle(" ");



        question = findViewById(R.id.QuestionTextView);
        numberofquestion = findViewById(R.id.QuestionNoTextView);
        OpctionsContainer = findViewById(R.id.Option_Container);
        nexrbutton = findViewById(R.id.NextBtn);

        list = new ArrayList<>();
        list.add(new QuestionsAdapter("1. Who developed Python Programming Language?","Wick van Rossum","Rasmus Lerdorf","Guido van Rossum","Niene Stom","Guido van Rossum"));
        list.add(new QuestionsAdapter("Which type of Programming does Python support","object-oriented programming","structured programming","functional programming","all of the mentioned","all of the mentioned"));
        list.add(new QuestionsAdapter("Is Python case sensitive when dealing with identifiers?","no","yes","machine dependent","none of the mentioned","yes"));
        list.add(new QuestionsAdapter("Which of the following is the correct extension of the Python file?",".python","pl",".py",".p",".py"));
        list.add(new QuestionsAdapter("Is Python code compiled or interpreted?","Python code is both compiled and interpreted","Python code is neither compiled nor interpreted","Python code is only compiled","Python code is only interpreted","Python code is both compiled and interpreted"));
        list.add(new QuestionsAdapter("All keywords in Python are in _________","Capitalized","lower case","UPPER CASE","None of the mentioned","None of the mentioned"));
        list.add(new QuestionsAdapter("Which of the following is used to define a block of code in Python language?","Indentation","Key","Brackets","All of the mentioned","Indentation"));
        list.add(new QuestionsAdapter("Which keyword is used for function in Python language?","Function","def","def","Define","def"));
        list.add(new QuestionsAdapter("Which of the following character is used to give single-line comments in Python?","//","#","!","/*","#"));
        list.add(new QuestionsAdapter("Which of the following functions can help us to find the version of python that we are currently working on?","sys.version(1)","sys.version(0)","sys.version(","sys.version","sys.version"));
        list.add(new QuestionsAdapter("Python supports the creation of anonymous functions at runtime, using a construct called __________","pi","anonymous","lambda","none of the mentioned","lambda"));
        list.add(new QuestionsAdapter("What does pip stand for python?","Pip Installs Python","Pip Installs Packages","Preferred Installer Program","All of the mentioned","Preferred Installer Program"));
        list.add(new QuestionsAdapter("Which of the following is the truncation division operator in Python?","|","//","//","%","//"));
        list.add(new QuestionsAdapter("What will be the output of the following Python code?\n" +
                "\n" +
                "l=[1, 0, 2, 0, 'hello', '', []]\n" +
                "list(filter(bool, l)))","[1, 0, 2, ‘hello’, ”, []]","Error","[1, 2, ‘hello’]","[1, 0, 2, 0, ‘hello’, ”, []]","[1, 2, ‘hello’]"));
        list.add(new QuestionsAdapter("Which of the following functions is a built-in function in python?","factorial()","print()","seed()","sqrt()","print()"));
        list.add(new QuestionsAdapter("Which of the following is the use of id() function in python?","Every object doesn’t have a unique id","Allows duplicate values","Data type with unordered values","Id returns the identity of the object","Id returns the identity of the object"));
        list.add(new QuestionsAdapter("Which of these about a frozenset is not true?","Mutable data type"," Id returns the identity of the object","All of the mentioned","None of the mentioned","Mutable data type"));
        list.add(new QuestionsAdapter("What will be the output of the following Python function?\n" +
                "\n" +
                "min(max(False,-3,-4), 2,7)\n)","-4","-3","2","False","False"));
        list.add(new QuestionsAdapter("Which of the following is not a core data type in Python programming?","Tuples","Lists","Class","Dictionary","Class"));
        list.add(new QuestionsAdapter("What will be the output of the following Python expression if x=56.236?\n" +
                "\n" +
                "print(\"%.2f\"%x)","56.236","56.23","56.0000","56.24","56.24"));


        for (int i = 0; i < 4; i++) {
            OpctionsContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkans((Button) view);
                }
            });
        }


        playanim(question,0,list.get(position).getQuestions());

        nexrbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nexrbutton.setEnabled(false);
                nexrbutton.setAlpha(0.7f);
                enableoption(true);
                position++;
                if (position == list.size()) {
                    Intent scoreintent = new Intent(PythonQuiz1.this, ResultActivity.class);
                    scoreintent.putExtra("score" , score);
                    scoreintent.putExtra("total" , list.size());
                    startActivity(scoreintent);
                    finish();
                    return;
                }
                count = 0;
                playanim(question,0, list.get(position).getQuestions());
            }
        });



    }

    private void playanim(View view, int value,final String data) {
        view.animate().alpha(value).scaleX(value).scaleY(value).setDuration(500).setStartDelay(100).setInterpolator(new DecelerateInterpolator()).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(@NonNull Animator animator) {
                if (value == 0 && count < 4) {
                    String option = "";
                    if (count == 0) {
                        option = list.get(position).getOptionA();
                    } else if (count == 1) {
                        option = list.get(position).getOptionB();
                    } else if (count == 2) {
                        option = list.get(position).getOptionC();
                    } else if (count == 3) {
                        option = list.get(position).getOptionD();
                    }
                    playanim(OpctionsContainer.getChildAt(count),0, option);
                    count++;
                }
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                if (value == 0) {
                    try {
                        ((TextView)view).setText(data);
                        numberofquestion.setText(position+1+"/"+list.size());
                    } catch (ClassCastException ex) {
                        ((Button)view).setText(data);
                    }
                    view.setTag(data);
                    playanim(view,1,data);
                }
            }

            @Override
            public void onAnimationCancel(@NonNull Animator animator) {

            }

            @Override
            public void onAnimationRepeat(@NonNull Animator animator) {

            }
        });
    }

    private void checkans(Button selectedOption) {
        enableoption(false);
        nexrbutton.setEnabled(true);
        nexrbutton.setAlpha(1);
        if (selectedOption.getText().toString().equals(list.get(position).getCorrectans())) {
            score++;
            selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#90EE90")));
        } else {
            selectedOption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#ff0000")));
            Button correctoption = (Button) OpctionsContainer.findViewWithTag(list.get(position).getCorrectans());
            correctoption.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#90EE90")));
        }
    }

    private void enableoption(boolean enable) {
        for (int i = 0; i < 4; i++) {
            OpctionsContainer.getChildAt(i).setEnabled(enable);
            if (enable) {
                OpctionsContainer.getChildAt(i).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#FFFFFF")));
            }
        }
    }
}