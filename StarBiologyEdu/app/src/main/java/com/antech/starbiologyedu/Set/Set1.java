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

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.starproduction.starbiology.Activity.ResultActivity;
import com.starproduction.starbiology.ModelClass.QuizModelClass;
import com.starproduction.starbiology.R;

import java.util.ArrayList;
import java.util.List;

public class Set1 extends AppCompatActivity {
    private TextView question, numberofquestion;
    private FloatingActionButton Bookmarkbtn;
    private LinearLayout OpctionsContainer;
    private Button sharebtn, nextbutton;
    private int count = 0;
    private List<QuizModelClass> list;
    private int position = 0;
    private int score = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.quiz_layout);

        question = findViewById(R.id.QuestionTextView);
        numberofquestion = findViewById(R.id.QuestionNoTextView);
        OpctionsContainer = findViewById(R.id.Option_Container);
        nextbutton = findViewById(R.id.NextBtn);


        list = new ArrayList<>();


        list.add(new QuizModelClass("The term used for transfer of pollen grains from anthers of one plant to stigma of a different plant which, during pollination, brings genetically different types of pollen grains to stigma, is:", "Cleistogamy", "Xenogamy", "Geitonogamy", "Chasmogamy", "Xenogamy"));
        list.add(new QuizModelClass("A typical angiosperm embryo sac at maturity is ", "8-nucleate and 8-celled", "8-nucleate and 7-celled", "7-nucleate and 8-celled", "7-nucleate and 7-celled", "8-nucleate and 7-celled"));
        list.add(new QuizModelClass("In water hyacinth and water lily pollination takes place by", "water currents only", "wind and water", "insects and water", "insects or wind", "insects or wind"));
        list.add(new QuizModelClass("The body of the ovule is fused within the funicle at", "micropyle", "nucellus", " chalaza", "hilum", "hilum"));
        list.add(new QuizModelClass("In some plants thalamus contributes to fruit formation. Such are termed as ", "false fruit", "aggregate fruit", "true fruit", "parthenocarpic fruit", "false fruit"));
        list.add(new QuizModelClass("Which of the following is incorrect for wind pollinated plants?", "Well exposed stamens and stigma", "Many ovules in each ovary", "Flowers are small and not brightly coloured", "Pollen grains are light and non-sticky", "Many ovules in each ovary"));
        list.add(new QuizModelClass("Which is the most common type of embryo sac in angiosperms?", "Tetrasporic with one mitotic stage of divisions", "Monosporic with three sequential mitotic divisions", "Monosporic with two sequential mitotic divisions", "Bisporic with two sequential mitotic divisions", "Monosporic with three sequential mitotic divisions"));
        list.add(new QuizModelClass("A plant being eaten by an herbivorous which in turn is eaten by a carnivorous makes", "Food chain", "Food web ", "Omnivorous", "Interdependent", "Food chain"));
        list.add(new QuizModelClass("The bacteria these good on dead organic matter are ", "Producers", "Herbivores ", "Carnivores", "Decomposers", "Decomposers"));
        list.add(new QuizModelClass("The ecosystem consists of ", "Producers", "Consumers", "Decomposers ", "All of these", "All of these"));
        list.add(new QuizModelClass("Green plants constitute ", "First trophic level", "Second trophic level ", "Third trophic level ", "Complete food chain", "First trophic level"));
        list.add(new QuizModelClass("An ecosystem is a complex interacting system of", "Individual", "Population", "Communities and their physical environment", "Communities and their soil conditions", "Communities and their physical environment"));
        list.add(new QuizModelClass("Eutrophic lakes means ", "Lake poor in nutrients", "Lake rich in nutrients", "Lake poor in flora and fauna ", "Lake lacking in water", "Lake rich in nutrients"));
        list.add(new QuizModelClass("First link in any food chain is a green plant because ", "Green plants can synthesize food", "They can eat everything ", " Fixed at one place", "None of the aboveg ", "Green plants can synthesize food"));
        list.add(new QuizModelClass("Development of Pollen grain from microsporogenous tissue is known as male _________.", "Microsporogenesis", "Apomixis", "Asexual reproduction", "Sexual reproduction", "Microsporogenesis"));
        list.add(new QuizModelClass("A true breeding plant is", "one that is able to breed on its own", "produced due to cross-pollination among unrelated plants", "homozygous and produces offspring of its own kind", "always homozygous recessive in its genetic constitution", "homozygous and produces offspring of its own kind"));


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
                    Intent scoreintent = new Intent(Set1.this, ResultActivity.class);
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