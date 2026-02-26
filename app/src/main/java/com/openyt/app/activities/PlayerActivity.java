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

/**
 * Tela do player de vídeo com ExoPlayer + HLS.
 */
public class PlayerActivity extends AppCompatActivity {

    private StyledPlayerView playerView;
    private ExoPlayer player;
    private LinearLayout playerErrorLayout;
    private TextView txtTitle;
    private TextView txtChannel;
    private TextView txtViews;
    private TextView txtDescription;
    private TextView btnShowMore;

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

        playerView       = findViewById(R.id.player_view);
        playerErrorLayout = findViewById(R.id.player_error_layout);
        txtTitle         = findViewById(R.id.txt_title);
        txtChannel       = findViewById(R.id.txt_channel);
        txtViews         = findViewById(R.id.txt_views);
        txtDescription   = findViewById(R.id.txt_description);
        btnShowMore      = findViewById(R.id.btn_show_more);

        videoId = getIntent().getStringExtra("video_id");
        String title   = getIntent().getStringExtra("video_title");
        String channel = getIntent().getStringExtra("video_channel");
        long views     = getIntent().getLongExtra("video_views", 0);

        txtTitle.setText(title != null ? title : "");
        txtChannel.setText(channel != null ? channel : "");
        txtViews.setText(getString(R.string.views, formatViews(views)));

        btnShowMore.setOnClickListener(v -> {
            if (descriptionExpanded) {
                txtDescription.setMaxLines(3);
                btnShowMore.setText(R.string.show_more);
            } else {
                txtDescription.setMaxLines(Integer.MAX_VALUE);
                btnShowMore.setText(R.string.show_less);
            }
            descriptionExpanded = !descriptionExpanded;
        });

        initPlayer();
        loadVideoInfo();
    }

    private void initPlayer() {
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);

        // URL do HLS master
        String hlsUrl = ApiClient.getInstance().getStreamUrl(videoId);
        MediaItem mediaItem = MediaItem.fromUri(Uri.parse(hlsUrl));
        player.setMediaItem(mediaItem);
        player.prepare();
        player.play();

        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                playerErrorLayout.setVisibility(View.VISIBLE);
            }
        });
    }

    private void loadVideoInfo() {
        ApiClient.getInstance().getVideoInfo(videoId, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(final JsonObject response) {
                mainHandler.post(() -> {
                    try {
                        JsonObject video = response.getAsJsonObject("video");
                        if (video == null) return;
                        if (video.has("title") && !video.get("title").isJsonNull())
                            txtTitle.setText(video.get("title").getAsString());
                        if (video.has("channel") && !video.get("channel").isJsonNull())
                            txtChannel.setText(video.get("channel").getAsString());
                        if (video.has("views") && !video.get("views").isJsonNull()) {
                            long v = video.get("views").getAsLong();
                            txtViews.setText(getString(R.string.views, formatViews(v)));
                        }
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
    protected void onPause() {
        super.onPause();
        if (player != null) player.pause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (player != null) {
            player.release();
            player = null;
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }

    private String formatViews(long views) {
        if (views >= 1_000_000) return String.format("%.1fM", views / 1_000_000.0);
        if (views >= 1_000)     return String.format("%.0fK", views / 1_000.0);
        return String.valueOf(views);
    }
}
