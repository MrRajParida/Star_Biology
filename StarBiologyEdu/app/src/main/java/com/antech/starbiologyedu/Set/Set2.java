package com.antech.starbiologyedu.Set;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.starproduction.starbiology.Activity.ResultActivity;
import com.starproduction.starbiology.ModelClass.QuizModelClass;
import com.starproduction.starbiology.R;

import java.util.ArrayList;
import java.util.List;

public class Set2 extends AppCompatActivity {
    private TextView question, numberofquestion;
    private FloatingActionButton Bookmarkbtn;
    private LinearLayout OpctionsContainer;
    private Button nextbutton;
    private int count = 0;
    private List<QuizModelClass> list;
    private int position = 0;
    private int score = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.quiz_layout);


        question = findViewById(R.id.QuestionTextView);
        numberofquestion = findViewById(R.id.QuestionNoTextView);
        OpctionsContainer = findViewById(R.id.Option_Container);
        nextbutton = findViewById(R.id.NextBtn);


        list = new ArrayList<>();


        list.add(new QuizModelClass("A tall true breeding garden pea plant is crossed with a dwarf true breeding garden pea \n" + "plant. When the F1 plants were selfed the resulting genotypes were in the ratio of :", "1 : 2 : 1 :: Tall heterozygous : Tall homogenous Dwarf", "3 : 1 :: Tall : Dwarf", "3 : 1 :: Dwarf : Tall", "1 : 2 : 1 :: Tall homogenous : Tall heterogenous : Dwarf", "1 : 2 : 1 :: Tall homogenous : Tall heterogenous : Dwarf"));
        list.add(new QuizModelClass("Close end of ovule is _________","Hybridization ","Chalaza"," Allogamy","Autogamy","Chalaza"));
        list.add(new QuizModelClass("Entry of Pollen tube through chalazal end is _________","Anatropous","Orthotropous","Chalazogamy","Campylotropous","Chalazogamy"));
        list.add(new QuizModelClass("Seed develops from _________","micropyle","nucellus","chalaza","Ovule","Ovule"));
        list.add(new QuizModelClass("Remnant of nucellus is known as __________","Ovule","Micropyle","Self sterility ","Protandry","Micropyle"));
        list.add(new QuizModelClass("Opening of ovule is ____________","Hybridization ","Chalaza","Allogamy","Autogamy","Chalaza"));
        list.add(new QuizModelClass("Nutritive tissue supply food to ovul is __________","Placenta","nucellus","chalaza","Ovule","Placenta"));
        list.add(new QuizModelClass("Nutritive tissue supply food for development of Pollen grain is __________","micropyle","Tapetum","chalaza","Ovule","Tapetum"));
        list.add(new QuizModelClass("An Ecosystem is ","Open", "Closed", "Both open and close", "Neither open nor closed", "Closed") );
        list.add(new QuizModelClass("If the plant producer dies in the ecosystem, then the system is ","Seriously affected", "Cannot produce food", "Can have more producers", "Hardly affected", "Seriously affected") );
        list.add(new QuizModelClass("The character of an ecosystem is determined by the environmental factor which is shortest supply. This is the", "Law of minimum", "Law of diminishing returns ", "Law of limiting factors", "Law of supply and demand", "Law of minimum"));
        list.add(new QuizModelClass("The trophic level of lion in a forest ecosystem is ", "T3", "T4", "T2 ", "T1", "T3"));
        list.add(new QuizModelClass("What energy percentage can be captured by the organisms of next trophic level", "20% ", "30%", "90%", " 10%", "30%"));
        list.add(new QuizModelClass("In a pond if there is too much wastage, then the BOD of pond will", "Increase", "Decrease", "Remain same ", "(A) and (b) both", "(A) and (b) both"));
        list.add(new QuizModelClass("Which of the following abundantly occurs in pond ecosystem? ", "Producer", "Consumer", "Top consumer","Decomposers","Producer"));
        list.add(new QuizModelClass("Which of the following is the most stable ecosystem? ", "Mountain", "Desert ", "Forest","Ocean","Mountain"));


        for (int i = 0; i < 4; i++) {
            OpctionsContainer.getChildAt(i).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    checkans((Button) view);
                }
            });
        }


        playanim(question, 0, list.get(position).getQuestions());

        nextbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nextbutton.setEnabled(false);
                nextbutton.setAlpha(0.7f);
                enableoption(true);
                position++;
                if (position == list.size()) {
                    Intent scoreintent = new Intent(Set2.this, ResultActivity.class);
                    scoreintent.putExtra("score", score);
                    scoreintent.putExtra("total", list.size());
                    startActivity(scoreintent);
                    finish();
                    return;
                }
                count = 0;
                playanim(question, 0, list.get(position).getQuestions());
            }
        });


    }

    private void playanim(View view, int value, final String data) {
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
                    playanim(OpctionsContainer.getChildAt(count), 0, option);
                    count++;
                }
            }

            @Override
            public void onAnimationEnd(@NonNull Animator animator) {
                if (value == 0) {
                    try {
                        ((TextView) view).setText(data);
                        numberofquestion.setText(position + 1 + "/" + list.size());
                    } catch (ClassCastException ex) {
                        ((Button) view).setText(data);
                    }
                    view.setTag(data);
                    playanim(view, 1, data);
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
        nextbutton.setEnabled(true);
        nextbutton.setAlpha(1);
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