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

public class JavaQuiz1 extends AppCompatActivity {
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
        setContentView(R.layout.activity_java_quiz1);

        this.setTitle(" ");
        question = findViewById(R.id.QuestionTextView);
        numberofquestion = findViewById(R.id.QuestionNoTextView);
        OpctionsContainer = findViewById(R.id.Option_Container);
        nexrbutton = findViewById(R.id.NextBtn);

        list = new ArrayList<>();
        list.add(new QuestionsAdapter("Who invented Java Programming?","Guido van Rossum","James Gosling","Dennis Ritchie","Bjarne Stroustrup","James Gosling"));
        list.add(new QuestionsAdapter("Which statement is true about Java?","Java is a sequence-dependent programming language","Java is a code dependent programming language","Java is a platform-dependent programming language"," Java is a platform-independent programming language"," Java is a platform-independent programming language"));
        list.add(new QuestionsAdapter("Which component is used to compile, debug and execute the java programs?","JRE","JIT","JDK","JVM","JDK"));
        list.add(new QuestionsAdapter("Which class provides system independent server side implementation?","Server","ServerReader","Socket","ServerSocket","ServerSocket"));
        list.add(new QuestionsAdapter("What is the numerical range of a char data type in Java?","0 to 256","-128 to 127","0 to 65535","0 to 32767","0 to 65535"));
        list.add(new QuestionsAdapter("Which one of the following is not an access modifier?","Protected","Void","Private","Public","Void"));
        list.add(new QuestionsAdapter("Which of these keywords are used for the block to be examined for exceptions?","check","throw","catch","try","try"));
        list.add(new QuestionsAdapter("Which of these packages contains the exception Stack Overflow in Java?","java.io","java.system","java.lang","java.util","java.lang"));
        list.add(new QuestionsAdapter("Which of the below is not a Java Profiler?","JProfiler","Eclipse Profiler","JVM","JConsole","JVM"));
        list.add(new QuestionsAdapter("Which of the following is a superclass of every class in Java?","ArrayList","String","Object class","Abstract class","Object class"));
        list.add(new QuestionsAdapter("Which of these keywords is used to define interfaces in Java?","intf","Intf","interface","Interface","interface"));
        list.add(new QuestionsAdapter("Which exception is thrown when java is out of memory?","MemoryError","OutOfMemoryError","MemoryFullException","MemoryOutOfBoundsException","OutOfMemoryError"));
        list.add(new QuestionsAdapter("Which of these are selection statements in Java?","break","continue","for()","if()","if()"));
        list.add(new QuestionsAdapter("What is the extension of compiled java classes?",".txt","js",".class",".java",".class"));
        list.add(new QuestionsAdapter("Which of the following is a type of polymorphism in Java Programming?","Compile time polymorphism","Multiple polymorphism","Execution time polymorphism","Multilevel polymorphism","Compile time polymorphism"));
        list.add(new QuestionsAdapter("What is Truncation in Java?","Boolean value assigned to floating type","Integer value assigned to floating type","Floating-point value assigned to an integer type","Floating-point value assigned to a Floating type","Floating-point value assigned to an integer type"));
        list.add(new QuestionsAdapter("What is not the use of “this” keyword in Java?","Referring to the instance variable when a local variable has the same name","Passing itself to the method of the same class","Passing itself to another method","Calling another constructor in constructor chaining","Passing itself to the method of the same class"));
        list.add(new QuestionsAdapter("Which of the following is not an OOPS concept in Java?","Polymorphism","Compilation","Inheritance","Encapsulation","Compilation"));
        list.add(new QuestionsAdapter("What is the the full form of JDBC?","Java Database Connectivity","Java Datebase Conectivity","Java Database Connectiviti","Java Dattabase Connectivity","Java Database Connectivity"));
        list.add(new QuestionsAdapter("Which environment variable is used to set the java path?","JavaPATH","JAVA_HOME","JAVA","MAVEN_Path","JAVA_HOME"));


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
                    Intent scoreintent = new Intent(JavaQuiz1.this, ResultActivity.class);
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