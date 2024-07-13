package com.example.edudy;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

public class AccountViewPagerAdapter extends FragmentStateAdapter {

    public AccountViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public AccountViewPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public AccountViewPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new AccountVideoBookmarkFragment();
            case 0:
            default:
                return new AccountBookmarkedNoteFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
