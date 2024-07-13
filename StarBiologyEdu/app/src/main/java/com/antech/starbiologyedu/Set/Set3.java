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

public class Set3 extends AppCompatActivity {
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



        list.add(new QuizModelClass("Chromosomal theory of inheritance was proposed by", "Sutton and Boveri", "Bateson and Punnett", "TH Morgan", "Watson and Crick", "Sutton and Boveri"));
        list.add(new QuizModelClass("Transfer of energy from one trophic level to other trophic level is according to the second law of thermodynamics. The efficiency of energy transfer from herbivorous to carnivorous is", "25%", "50% ", "10% ","5%","5%"));
        list.add(new QuizModelClass("How many true breeding pea plant varieties did Mendel select as pairs, which were similar except in one character with contrasting traits?", "2", "14", "8", "4", "14"));
        list.add(new QuizModelClass("Identify the indirect statement with reference to the gene ‘I’ controls ABO blood groups.", " A person will have only two of the three alleles", "When IA and IB are present together, they express same type of sugar", "Allele ‘I’ does not produce any sugar", "The gene (I) has three alleles", "When IA and IB are present together, they express same type of sugar"));
        list.add(new QuizModelClass("The number of contrasting characters studied by Mendel for his experiments was", "14", "4", "2", "7", "7"));
        list.add(new QuizModelClass("The best example for pleiotropy is", "skin colour", "phenylketonuria", "colour blindness", " ABO blood group", "phenylketonuria"));
        list.add(new QuizModelClass("The production of gametes by the parents, the formation of zygotes, the F1 and F2 plants, can be understood using", " pie diagram", "a  pyramid diagram", "Punnett square", " Venn diagram", "Punnett square"));
        list.add(new QuizModelClass("In Antirrhinum (snapdragon), a red flower was crossed with a white flower and in F1-generation, pink flowers were obtained. When pink flower were selfed, the F2- generation showed white, red and pink flowers", "Pink colour in F1is due to incomplete dominance", "Ratio of F2 is 1/4 (Red) : 2/4 (Pink) : 1/4 (White)", " Law of segregation does not apply in this experiment", "This experiment does not follow the principle of dominance", "Ratio of F2 is 1/4 (Red) : 2/4 (Pink) : 1/4 (White)"));
        list.add(new QuizModelClass("Which one of the following pairs is incorrectly matched?", "XO type sex-determination - Grasshopper", "ABO blood grouping - Codominance", "Starch synthesis in pea - Multiple alleles", "TH Morgan - Linkage", "Starch synthesis in pea - Multiple alleles"));
        list.add(new QuizModelClass("Match the following columns and choose the correct option from the codes given below. \n" +
                "Column-I Column-II\n" +
                "A. Pleiotropic gene 1. Both alleles express equally\n" +
                "B. Codominance 2. Change in nucleotides\n" +
                "6\n" +
                "C. Epistasis 3. One gene shows multiple\n" +
                "phenotypic expression\n" +
                "D. Mutation 4. Non-allelic gene inheritance", "1 2 3 4", "2 3 4 1", "3 1 4 2", "1 3 4 2", "3 1 4 2"));
        list.add(new QuizModelClass("Select the correct statement", "Spliceosomes take part in translation", "Punnett square was developed by a British scientist", "Franklin Stahl coined the term 'linkage'", "Transduction was discovered by S Akman", "Punnett square was developed by a British scientist"));
        list.add(new QuizModelClass("An ecosystemresists changes becauseit is in astate of ", "Imbalance", "Homeostasis", "Shortage of components", " Deficiency of light", "Shortage of components"));
        list.add(new QuizModelClass("In an ecosystem, the population of", "Primary producers are more than that of primary consumers", "Secondary consumers are largest because they are powerful", " Primary consumers out number primary producers", "Primary consumers are least dependent upon primary producers ", "Secondary consumers are largest because they are powerful"));
        list.add(new QuizModelClass("Which of the following acts as ‘‘nature’s scavengers’’", "Man", "Animals ", "Insects", "Micro-organisms", "Man"));
        list.add(new QuizModelClass("Which is the correct sequence in the food chain in grassland? ", "Grass wolf deer buffalo", "Bacteria grass rabbit wolf ", "Grass insect birdssnakes ", "Grass snake insect deer", "Grass wolf deer buffalo"));
        list.add(new QuizModelClass("The pyramid that cannot be inverted in a stable ecosystem, is pyramid of ", "Number", "Energy", "Biomass", "All the above", "All the above"));


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
                    Intent scoreintent = new Intent(Set3.this, ResultActivity.class);
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