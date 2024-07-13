package com.antech.starbiologyedu.Activity;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
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
import com.starproduction.starbiology.R;

public class VideoViewActivity extends AppCompatActivity {
    private StyledPlayerView playerView;
    private ExoPlayer player;
    private ProgressBar progressBar;
    private String videoLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_view);

        // Set the activity to fullscreen mode
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        playerView = findViewById(R.id.player_view);
        progressBar = findViewById(R.id.loadVideoWithDescription);

        // Set the progress bar color
        progressBar.getIndeterminateDrawable().setColorFilter(
                ContextCompat.getColor(this, R.color.app_yellow),
                android.graphics.PorterDuff.Mode.SRC_IN);

        // Get the video link from the intent
        videoLink = getIntent().getStringExtra("videolink");

        if (videoLink != null) {
            initializePlayer(videoLink);
        } else {
            Toast.makeText(this, "Video link is missing", Toast.LENGTH_SHORT).show();
        }
    }

    private void initializePlayer(String videoUrl) {
        // Configure buffering settings
        LoadControl loadControl = new DefaultLoadControl.Builder()
                .setAllocator(new DefaultAllocator(true, C.DEFAULT_BUFFER_SEGMENT_SIZE))
                .setBufferDurationsMs(
                        50000, // minBufferMs
                        100000, // maxBufferMs
                        1500, // bufferForPlaybackMs
                        3000 // bufferForPlaybackAfterRebufferMs
                )
                .setTargetBufferBytes(-1)
                .setPrioritizeTimeOverSizeThresholds(true)
                .build();

        // Initialize ExoPlayer with load control
        player = new ExoPlayer.Builder(this)
                .setLoadControl(loadControl)
                .build();

        playerView.setPlayer(player);

        Uri videoUri = Uri.parse(videoUrl);
        MediaItem mediaItem = MediaItem.fromUri(videoUri);
        player.setMediaItem(mediaItem);
        player.prepare();

        // Add listener for player state changes
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

    @Override
    protected void onStart() {
        super.onStart();
        if (player != null) {
            player.prepare(); // Prepare the player again if needed
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (player != null && player.getPlaybackState() == Player.STATE_READY) {
            player.play(); // Resume playback when activity is resumed
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (player != null) {
            player.pause(); // Pause playback when activity is paused
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (player != null) {
            player.release(); // Release player resources
            player = null;
        }
    }
}