package com.antech.starbiology.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.antech.starbiology.QuestionList.CbseFragment;
import com.antech.starbiology.QuestionList.ChseBoardFragment;
import com.antech.starbiology.QuestionList.NursingFragment;
import com.antech.starbiology.QuestionList.OuatFragment;
import com.antech.starbiology.QuestionList.NeetFragment;

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