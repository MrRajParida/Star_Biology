package com.antech.starbiology.Adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.antech.starbiology.Note.NotesBookmarking;
import com.antech.starbiology.Question.QuestionsBookmarking;
import com.antech.starbiology.Video.VideosBookmarking;

public class BookmarkPagerAdapter extends FragmentStateAdapter {
    public BookmarkPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    public BookmarkPagerAdapter(@NonNull Fragment fragment) {
        super(fragment);
    }

    public BookmarkPagerAdapter(@NonNull FragmentManager fragmentManager, @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 2:
                return new QuestionsBookmarking();
            case 1:
                return new VideosBookmarking();
            case 0:
            default:
                return new NotesBookmarking();
        }
    }

    @Override
    public int getItemCount() {
        return 3;
    }
}
