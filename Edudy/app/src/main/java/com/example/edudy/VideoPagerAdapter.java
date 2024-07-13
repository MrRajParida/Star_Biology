package com.example.edudy;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class VideoPagerAdapter extends FragmentStateAdapter {
    public VideoPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public VideoPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public VideoPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new BackendVideoFragment();
            case 0:
            default:
                return new FrontendVideoFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
