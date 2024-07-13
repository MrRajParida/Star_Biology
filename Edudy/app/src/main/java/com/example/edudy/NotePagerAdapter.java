package com.example.edudy;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class NotePagerAdapter extends FragmentStateAdapter {

    public NotePagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public NotePagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public NotePagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new BackendNoteFragment();
            case 0:
            default:
                return new FrontendNoteFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 2;
    }
}
