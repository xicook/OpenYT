package com.openyt.app.activities;

import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.AdapterView;
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
    private TextView txtTitle, txtChannel, txtDescription, btnShowMore;
    private Spinner spinnerQuality;
    private Button btnOrientation;
    private String videoId;
    private String currentQuality = "480";
    private boolean descriptionExpanded = false;
    private boolean isLandscape = false;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    private static final String[] QUALITIES     = {"240p", "360p", "480p", "720p", "1080p"};
    private static final String[] QUALITY_VALUES = {"240",  "360",  "480",  "720",  "1080"};

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
        txtDescription    = findViewById(R.id.txt_description);
        btnShowMore       = findViewById(R.id.btn_show_more);
        spinnerQuality    = findViewById(R.id.spinner_quality);
        btnOrientation    = findViewById(R.id.btn_orientation);

        videoId = getIntent().getStringExtra("video_id");
        String title   = getIntent().getStringExtra("video_title");
        String channel = getIntent().getStringExtra("video_channel");
        txtTitle.setText(title != null ? title : "");
        txtChannel.setText(channel != null ? channel : "");

        // Spinner de qualidade
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
            android.R.layout.simple_spinner_item, QUALITIES);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerQuality.setAdapter(adapter);
        spinnerQuality.setSelection(2); // 480p default
        spinnerQuality.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            boolean first = true;
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (first) { first = false; return; }
                currentQuality = QUALITY_VALUES[position];
                loadDirectUrl();
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });

        // Botão de orientação
        btnOrientation.setOnClickListener(v -> toggleOrientation());

        // Mostrar mais/menos descrição
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

        // Inicializa player
        player = new ExoPlayer.Builder(this).build();
        playerView.setPlayer(player);
        player.addListener(new Player.Listener() {
            @Override
            public void onPlayerError(PlaybackException error) {
                mainHandler.post(() -> playerErrorLayout.setVisibility(View.VISIBLE));
            }
        });

        loadDirectUrl();
        loadVideoInfo();
    }

    private void loadDirectUrl() {
        playerErrorLayout.setVisibility(View.GONE);
        ApiClient.getInstance().getDirectUrl(videoId, currentQuality, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JsonObject response) {
                mainHandler.post(() -> {
                    try {
                        String url = response.get("url").getAsString();
                        long pos = player.getCurrentPosition();
                        player.setMediaItem(MediaItem.fromUri(Uri.parse(url)));
                        player.prepare();
                        if (pos > 0) player.seekTo(pos);
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

    private void toggleOrientation() {
        if (isLandscape) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            btnOrientation.setText("⛶ Paisagem");
            isLandscape = false;
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
            btnOrientation.setText("↩ Retrato");
            isLandscape = true;
        }
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
