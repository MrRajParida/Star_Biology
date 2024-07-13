package com.antech.starbiologyedu.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.starproduction.starbiology.QuestionList.CbseFragment;
import com.starproduction.starbiology.QuestionList.ChseBoardFragment;
import com.starproduction.starbiology.QuestionList.NeetFragment;
import com.starproduction.starbiology.QuestionList.NursingFragment;
import com.starproduction.starbiology.QuestionList.OuatFragment;

public class QuestionPagerAdapter extends FragmentStateAdapter {
    public QuestionPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 4:
                return new OuatFragment();
            case 3:
                return new NursingFragment();
            case 2:
                return new NeetFragment();
            case 1:
                return new CbseFragment();
            case 0:
            default:
                return new ChseBoardFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}