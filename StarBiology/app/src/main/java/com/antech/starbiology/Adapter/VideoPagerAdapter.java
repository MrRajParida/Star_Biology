package com.antech.starbiology.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.antech.starbiology.Video.VideoBotanyFragment;
import com.antech.starbiology.Video.VideoZoologyFragment;

public class VideoPagerAdapter extends FragmentStateAdapter {
    public VideoPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new VideoZoologyFragment();
            case 0:
            default:
                return new VideoBotanyFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
