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

public class Set5 extends AppCompatActivity {
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

        list.add(new QuizModelClass("In an ecosystem","Cycling of energy and nutrients is a coupled process","Cycling of energy is an independent process","Movement of energy is unidirectional","Macro and micronutrients cycle at the same pace","Movement of energy is unidirectional"));
        list.add(new QuizModelClass("In a recently fertilized ovule, the haploid, diploid and triploid conditions are respectively seen in","Endosperm, Nucellus, Egg"," Egg, Nucellus, Endosperm","Antipodals, Oospore, Primary Endosperm Nucleus","Polar Nuclei, secondary nucleus, Endosperm","Antipodals, Oospore, Primary Endosperm Nucleus"));
        list.add(new QuizModelClass("In sunflower, self pollination is avoided by","Protogyny"," Unisexuality","Self sterility ","Protandry","Protandry"));
        list.add(new QuizModelClass("Detritus food chain starts from", "Dead organic matter", "Green plants", "Zooplanktons", "None of the above", "Zooplanktons"));
        list.add(new QuizModelClass("In an ecosystem decomposer include ", "Bacteria and fungi", "Only microscopic organisms", "Above two", "Above two plus macro-organisms", "Only microscopic organisms"));
        list.add(new QuizModelClass("If phytoplankton are destroyed in the sea, then ","Bacteria and fungi", "Primary consumers will grow luxuriantly  ", "It will affect the food chain", "No effect will be seen", "It will affect the food chain"));
        list.add(new QuizModelClass("In a tree ecosystem, the pyramid of number is ","Upright", "Inverted", "Both of the above", "None of the above", "None of the above") );
        list.add(new QuizModelClass("Largest ecosystem of the world are ","Grasslands", "Great lakes ", "Oceans", "Forests", "Oceans") );
        list.add(new QuizModelClass("Energy store at consumer level is called ","Gross primary productivity", "Secondary productivity ", "Net primary productivity", "Net productivity", "Net primary productivity") );
        list.add(new QuizModelClass("Sexual reproduction leads to :","Polyploidy"," Recombination","Apomixis","Parthenogenesis","Polyploidy"));
        list.add(new QuizModelClass("In an embryo sac, the cells that degenerate after fertilization are :","Synergids and primary endosperm cell","Synergids and antipodals","Antipodals and primary endosperm cell","Egg and antipodals.","Synergids and antipodals"));
        list.add(new QuizModelClass("While planning for an artificial hybridization programme involving dioecious plants, which of the following steps would not be relevant:","Bagging of female flower","Dusting of pollen on stigma","Emasculation","Collection of pollen","Emasculation"));
        list.add(new QuizModelClass("In the embryos of a typical dicot and a grass, true homologous structures are :","Coleorhiza and coleoptile","Coleoptile and scutellum","Cotyledons and scutellum","Hypocotyl and radicle","Cotyledons and scutellum"));
        list.add(new QuizModelClass("In ecological crisis, whose interference play an important role", "Green plants", "Human", "Biotic and abiotic components", "None of these", "Human"));
        list.add(new QuizModelClass("Whale is","Primary producer", "Carnivorous secondary consumer", "A decomposer", "Herbivorous", "Carnivorous secondary consumer"));
        list.add(new QuizModelClass("Energy enters into the ecosystem through ", "Herbivores", "Carnivores ", "Producers", "Decomposers", "Producers"));


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
                    Intent scoreintent = new Intent(Set5.this, ResultActivity.class);
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