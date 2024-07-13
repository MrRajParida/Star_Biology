package com.antech.starbiology.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.antech.starbiology.Note.ZoologyNdYearFragment;
import com.antech.starbiology.Note.ZoologyStYearFragment;

public class SubZoologyNotePagerAdapter extends FragmentStateAdapter {
    public SubZoologyNotePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public SubZoologyNotePagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public SubZoologyNotePagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new ZoologyNdYearFragment();
            case 0:
            default:
                return new ZoologyStYearFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
