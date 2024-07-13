package com.antech.starbiologyedu.Question;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.starproduction.starbiology.Adapter.QuestionPagerAdapter;
import com.starproduction.starbiology.R;

public class QuestionActivity extends AppCompatActivity {
    private TabLayout tabLayout;
    private ViewPager2 viewPager2;
    private QuestionPagerAdapter questionPagerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_question);

        tabLayout = findViewById(R.id.tab_layout);
        viewPager2 = findViewById(R.id.view_pager);
        questionPagerAdapter = new QuestionPagerAdapter(this);
        viewPager2.setAdapter(questionPagerAdapter);

        new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> {
                    switch (position) {
                        case 0:
                            tab.setText("CHSE");
                            break;
                        case 1:
                            tab.setText("CBSE");
                            break;
                        case 2:
                            tab.setText("NEET");
                            break;
                        case 3:
                            tab.setText("NURSING");
                            break;
                        case 4:
                            tab.setText("OUAT");
                            break;
                    }
                }).attach();

    }
}