package com.antech.starbiology.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.antech.starbiology.Note.BotanyNdYearFragment;
import com.antech.starbiology.Note.BotanyStYearFragment;

public class SubBotanyNotePagerAdapter extends FragmentStateAdapter {
    public SubBotanyNotePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public SubBotanyNotePagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public SubBotanyNotePagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new BotanyNdYearFragment();
            case 0:
            default:
                return new BotanyStYearFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
