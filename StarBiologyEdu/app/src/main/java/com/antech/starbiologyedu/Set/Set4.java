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

public class Set4 extends AppCompatActivity {
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

        list.add(new QuizModelClass("Experimental verification of the chromosomal theory of inheritance was done by", "Sutton", "Boveri", "Morgan", "Mendel", "Morgan"));
        list.add(new QuizModelClass("The phenomenon observed in some plants wherein parts of the sexual apparatus is used for forming embryos without fertilization is called:","Parthenocarpy","Apomixis"," Vegetative propagation","Sexual reproduction.","Apomixis"));
        list.add(new QuizModelClass("In a flower, if the megaspore mother cell forms megaspores without undergoing meiosis and if one of the megaspores develops into an embryo sac, its nuclei would be :","Haploid","Diploid","A few haploid and a few diploid","With varying ploidy","Diploid"));
        list.add(new QuizModelClass("The phenomenon wherein, the ovary develops into a fruit without fertilization is called","Parthenocarpy","Apomixis","Asexual reproduction","Sexual reproduction","Parthenocarpy"));
        list.add(new QuizModelClass("Which of the following characteristics represents ‘inheritance of blood groups’ in \n" +
                "humans?\n" +
                "1. Dominance\n" +
                "2. Codominance\n" +
                "3. Multiple allele \n" +
                "4. Incomplete dominant\n" +
                "5. Polygenic inheritance", "2, 1 and 5", "1, 2 and 3", "2, 3 and 5", " 1, 3 and 5", "1, 2 and 3"));
        list.add(new QuizModelClass("AB blood group shows", "Codominance", "Incomplete dominance", "Polygenic inheritance", "Pleiotropy", "Codominance"));
        list.add(new QuizModelClass("In pea plants, green pod colour is dominant over yellow pods. 1000 seeds taken from a \n" +
                "pea plant on germination produce 760 green pod and 210 yellow pod plants. The parental \n" +
                "genotype and phenotype of the seed plant are", "heterozygous and yellow", "heterozygous and green", "homozygous and yellow", " homozygous and green", "heterozygous and green"));
        list.add(new QuizModelClass("Which one from those given below is the period of Mendel’s hybridization experiments?", "1856-1863", "1840-1850", "1857-1869", "1870-1877", "1856-1863"));
        list.add(new QuizModelClass("The genotypes of a husband and wife are and one among the blood types of their children, \n" +
                "how many different genotypes and phenotypes are possible?", "3 genotypes, 3 phenotypes", "3 genotypes, 4 phenotypes", "4 genotypes, 3 phenotypes", "4  genotypes, 4 phenotypes", "4 genotypes, 3 phenotypes"));
        list.add(new QuizModelClass("If two people with AB blood group marry and have sufficient large number of children, \n" +
                "these children could be classified as A blood group, AB blood group and B blood group in \n" +
                "1 : 2 : 1 ratio.Modern technique of protein electrophoresis reveals the presence of both A \n" +
                "and B type proteins in AB blood group individuals. This is an example of", "codominance", "incomplete dominance", "partial dominance", "complete dominance", "codominance"));
        list.add(new QuizModelClass("The process of removal of anther from the flower bud before it dehisces is called as","emasculation","bagging","embryo rescue","budding","emasculation"));
        list.add(new QuizModelClass("What is the fate of the male gametes discharged in the synergid?","All fuse with the egg","One fuses with the egg, other (s) fuse (s) with synergid nucleus","One fuses with the egg and other fuse with central cell nuclei"," One fuses with the egg other (s) degenerate (s) in the synergid","One fuses with the egg and other fuse with central cell nuclei"));
        list.add(new QuizModelClass("Which one of the following statements regarding post-fertilisation development in flowering plants is incorrect?"," Zygote develops into embryo","Central cell develops into endosperm","Ovules develop into embryo sac"," Ovary develops into fruit","Ovules develop into embryo sac"));
        list.add(new QuizModelClass("The cross pollination within the same species is also called","Hybridization ","Xenogamy"," Allogamy","Autogamy","Xenogamy"));
        list.add(new QuizModelClass("During fertilization male gametes are carried by pollen tube. This is called","Syngamy","Mesogamy"," Polygamy","Siphonogamy","Siphonogamy"));
        list.add(new QuizModelClass("In Angiosperms, the common type of ovule is :","Anatropous","Orthotropous","Hemianatropous","Campylotropous","Anatropous"));

      
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
                    Intent scoreintent = new Intent(Set4.this, ResultActivity.class);
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