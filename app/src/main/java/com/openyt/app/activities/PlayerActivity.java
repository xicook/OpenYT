package com.openyt.app.activities;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.PlaybackException;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.ui.StyledPlayerView;
import com.google.gson.JsonObject;
import com.openyt.app.R;
import com.openyt.app.network.ApiClient;

public class PlayerActivity extends AppCompatActivity {

    private StyledPlayerView playerView;
    private ExoPlayer player;
    private LinearLayout playerErrorLayout;
    private TextView txtTitle, txtChannel, txtViews, txtDescription, btnShowMore;
    private String videoId;
    private boolean descriptionExpanded = false;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("");
        }

        playerView        = findViewById(R.id.player_view);
        playerErrorLayout = findViewById(R.id.player_error_layout);
        txtTitle          = findViewById(R.id.txt_title);
        txtChannel        = findViewById(R.id.txt_channel);
        txtViews          = findViewById(R.id.txt_views);
        txtDescription    = findViewById(R.id.txt_description);
        btnShowMore       = findViewById(R.id.btn_show_more);

        videoId = getIntent().getStringExtra("video_id");
        txtTitle.setText(getIntent().getStringExtra("video_title") != null ? getIntent().getStringExtra("video_title") : "");
        txtChannel.setText(getIntent().getStringExtra("video_channel") != null ? getIntent().getStringExtra("video_channel") : "");

        btnShowMore.setOnClickListener(v -> {
            if (descriptionExpanded) {
                txtDescription.setMaxLines(3);
                btnShowMore.setText("Mostrar mais");
            } else {
                txtDescription.setMaxLines(Integer.MAX_VALUE);
                btnShowMore.setText("Mostrar menos");
            }
            descriptionExpanded = !descriptionExpanded;
        });

        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                playerErrorLayout.setVisibility(View.VISIBLE);
            }
        });

        loadDirectUrl();
        loadVideoInfo();
    }

    private void loadDirectUrl() {
        ApiClient.getInstance().getDirectUrl(videoId, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JsonObject response) {
                mainHandler.post(() -> {
                    try {
                        String url = response.get("url").getAsString();
                        player.setMediaItem(MediaItem.fromUri(Uri.parse(url)));
                        player.prepare();
                        player.play();
                    } catch (Exception e) {
                        playerErrorLayout.setVisibility(View.VISIBLE);
                    }
                });
            }
            @Override
            public void onError(String message) {
                mainHandler.post(() -> playerErrorLayout.setVisibility(View.VISIBLE));
            }
        });
    }

    private void loadVideoInfo() {
        ApiClient.getInstance().getVideoInfo(videoId, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JsonObject response) {
                mainHandler.post(() -> {
                    try {
                        JsonObject video = response.getAsJsonObject("video");
                        if (video == null) return;
                        if (video.has("title") && !video.get("title").isJsonNull())
                            txtTitle.setText(video.get("title").getAsString());
                        if (video.has("channel") && !video.get("channel").isJsonNull())
                            txtChannel.setText(video.get("channel").getAsString());
                        if (video.has("description") && !video.get("description").isJsonNull()) {
                            String desc = video.get("description").getAsString();
                            txtDescription.setText(desc);
                            btnShowMore.setVisibility(desc.length() > 100 ? View.VISIBLE : View.GONE);
                        }
                    } catch (Exception ignored) {}
                });
            }
            @Override
            public void onError(String message) {}
        });
    }

    @Override
    protected void onPause() { super.onPause(); if (player != null) player.pause(); }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) { player.release(); player = null; }
    }

    @Override
    public boolean onSupportNavigateUp() { onBackPressed(); return true; }
}
