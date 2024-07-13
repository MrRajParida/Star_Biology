package com.kprandro.newshow;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import de.hdodenhof.circleimageview.CircleImageView;

public class FullNewsActivity extends AppCompatActivity {

    private CircleImageView fullNewsUserImg;
    private TextView fullNewsUserId, fullNewsUserFollow, fullNewsTitle, fullNewsDescription;
    private ImageView fullNewsUserVerified, fullNewsImg;
    private ImageButton newsShare, newsBookmark;
    private boolean isFollowing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_news);

        int userImgResource = getIntent().getIntExtra("newsUserImg", 0);
        String userIdResource = getIntent().getStringExtra("newsUserId");
        Boolean userVerifiedResource = getIntent().getBooleanExtra("newsUserVerified", false);
        int newsImgResource = getIntent().getIntExtra("newsImg", 0);
        String newsTitleResource = getIntent().getStringExtra("newsTitle");
        String newsDescriptionResource = getIntent().getStringExtra("newsDescription");


        fullNewsUserImg = findViewById(R.id.userImage);
        fullNewsUserId = findViewById(R.id.userId);
        fullNewsUserVerified = findViewById(R.id.userVerified);
        fullNewsTitle = findViewById(R.id.newsTitle);
        fullNewsDescription = findViewById(R.id.newsDescription);
        fullNewsImg = findViewById(R.id.newsImg);
        fullNewsUserFollow = findViewById(R.id.userFollow);


        fullNewsUserImg.setImageResource(userImgResource);
        fullNewsUserId.setText(userIdResource);
        fullNewsTitle.setText(newsTitleResource);
        fullNewsDescription.setText(newsDescriptionResource);
        fullNewsImg.setImageResource(newsImgResource);

        if (userVerifiedResource) {
            fullNewsUserVerified.setVisibility(View.VISIBLE);
        } else {
            fullNewsUserVerified.setVisibility(View.GONE);
        }

        fullNewsUserFollow.setText("Follow");

        fullNewsUserFollow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isFollowing) {
                    fullNewsUserFollow.setText("Follow");
                    isFollowing = false;
                } else {
                    fullNewsUserFollow.setText("Following");
                    isFollowing = true;
                }
            }
        });
    }
}