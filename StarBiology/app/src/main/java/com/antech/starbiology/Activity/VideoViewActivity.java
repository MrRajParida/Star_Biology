package com.antech.starbiology.Activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;

import com.google.android.exoplayer2.C;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.android.exoplayer2.upstream.DefaultAllocator;
import com.antech.starbiology.R;

public class VideoViewActivity extends AppCompatActivity {
    private StyledPlayerView playerView;
    private ExoPlayer player;
    private ProgressBar progressBar;
    private String videoLink, titleText, descText;
    private TextView toolbarTitle;
    private Toolbar toolbar;
    private Handler handler;
    private Runnable hideControlsRunnable;
    private final int AUTO_HIDE_DELAY = 3000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(null);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.baseline_arrow_back_ios_24);
        }

        playerView = findViewById(R.id.player_view);
        progressBar = findViewById(R.id.loadVideoWithDescription);
        toolbarTitle = findViewById(R.id.videoToolbarText);
        toolbarTitle.setSelected(true);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        progressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.app_yellow),
                android.graphics.PorterDuff.Mode.SRC_IN);

        videoLink = getIntent().getStringExtra("videoLink");
        titleText = getIntent().getStringExtra("videoTitle");
        descText = getIntent().getStringExtra("videoDesc");

        playerView.setOnClickListener(v -> toggleToolbarAndControls());
        handler = new Handler();
        hideControlsRunnable = new Runnable() {
            @Override
            public void run() {
                hideToolbarAndControls();
            }
        };

        if (videoLink != null) {
            initializePlayer(videoLink);
            toolbarTitle.setText(titleText + ", " +descText);
        } else {
            Toast.makeText(this, "Video link is missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializePlayer(String videoUrl) {

        LoadControl loadControl = new DefaultLoadControl.Builder()
                .setAllocator(new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE))
                .setBufferDurationsMs(
                        50000,
                        100000,
                        1500,
                        3000
                )
                .setTargetBufferBytes(-1)
                .setPrioritizeTimeOverSizeThresholds(true)
                .build();

        player = new ExoPlayer.Builder(this)
                .setLoadControl(loadControl)
                .build();

        playerView.setPlayer(player);

        Uri videoUri = Uri.parse(videoUrl);
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        player.setMediaItem(mediaItem);
        player.prepare();

        player.addListener(new Player.Listener() {
            @Override
            public void onPlaybackStateChanged(int playbackState) {
                if (playbackState == Player.STATE_READY) {
                    progressBar.setVisibility(View.GONE);
                    player.play();
                } else if (playbackState == Player.STATE_BUFFERING) {
                    progressBar.setVisibility(View.VISIBLE);
                } else if (playbackState == Player.STATE_ENDED || playbackState == Player.STATE_IDLE) {
                    progressBar.setVisibility(View.GONE);
                }
            }

            @Override
            public void onPlayerError(PlaybackException error) {
                progressBar.setVisibility(View.GONE);
                String errorMessage = "Error playing video: " + error.getMessage();
                Toast.makeText(VideoViewActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleToolbarAndControls() {
        if (toolbar.getVisibility() == View.VISIBLE) {
            hideToolbarAndControls();
        } else {
            showToolbarAndControls();
            // Hide them again after a delay
            handler.postDelayed(hideControlsRunnable, AUTO_HIDE_DELAY);
        }
    }

    private void showToolbarAndControls() {
        toolbar.setVisibility(View.VISIBLE);
        toolbar.setAlpha(0.0f);
        toolbar.animate().alpha(1.0f).setDuration(300).start();

        // Show ExoPlayer controls
        playerView.showController();
    }

    private void hideToolbarAndControls() {
        toolbar.animate().alpha(0.0f).setDuration(300).withEndAction(() -> toolbar.setVisibility(View.GONE)).start();

        // Hide ExoPlayer controls
        playerView.hideController();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (player != null) {
            player.prepare();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null && player.getPlaybackState() == Player.STATE_READY) {
            player.play();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.pause();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}