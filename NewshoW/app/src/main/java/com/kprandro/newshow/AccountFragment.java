package com.kprandro.newshow;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import de.hdodenhof.circleimageview.CircleImageView;

public class AccountFragment extends Fragment {
    private CircleImageView acImg;
    private ImageView acVerified;
    private TextView acUserId, acUsername, acPost, acFollowers, acFollowing;
    private SharedViewModel sharedViewModel;
    public AccountFragment() {
        // Required empty public constructor
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_account, container, false);

        acUserId = view.findViewById(R.id.acUserId);
        acVerified = view.findViewById(R.id.acVerified);
        acImg = view.findViewById(R.id.acUserImg);
        acPost = view.findViewById(R.id.acPost);
        acFollowers = view.findViewById(R.id.acFollowers);
        acFollowing = view.findViewById(R.id.acFollowing);
        acUsername = view.findViewById(R.id.acUsername);


        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        sharedViewModel.getSelectedNewsItem().observe(getViewLifecycleOwner(), new Observer<NewsItemClass>() {
            @Override
            public void onChanged(NewsItemClass newsItem) {
                if (newsItem != null) {
                    acUserId.setText(newsItem.getUserId());
                    acUsername.setText(newsItem.getUserId());
                    acImg.setImageResource(newsItem.getUserImg());
                    acVerified.setVisibility(newsItem.getVerified() ? View.VISIBLE : View.GONE);
                }
            }
        });

        return view;
    }
}