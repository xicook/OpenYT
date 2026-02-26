package com.openyt.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.openyt.app.R;
import com.openyt.app.activities.PlayerActivity;
import com.openyt.app.adapters.VideoAdapter;
import com.openyt.app.models.Video;
import com.openyt.app.network.ApiClient;
import com.openyt.app.utils.JsonParser;

import java.util.List;

/**
 * Fragmento da tela inicial — exibe vídeos em destaque (trending).
 */
public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private LinearLayout layoutError;
    private TextView txtError;
    private Button btnRetry;

    private VideoAdapter adapter;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = view.findViewById(R.id.recycler_videos);
        progressBar  = view.findViewById(R.id.progress_bar);
        layoutError  = view.findViewById(R.id.layout_error);
        txtError     = view.findViewById(R.id.txt_error);
        btnRetry     = view.findViewById(R.id.btn_retry);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new VideoAdapter(getContext(), new VideoAdapter.OnVideoClickListener() {
            @Override
            public void onVideoClick(Video video) {
                openPlayer(video);
            }
        });
        recyclerView.setAdapter(adapter);

        btnRetry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadTrending();
            }
        });

        loadTrending();
        return view;
    }

    private void loadTrending() {
        showLoading();

        ApiClient.getInstance().getTrending("BR", new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(final JsonObject response) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!isAdded()) return;

                        try {
                            JsonArray videosArray = response.getAsJsonArray("videos");
                            List<Video> videos = JsonParser.parseVideoList(videosArray);

                            if (videos.isEmpty()) {
                                showError(getString(R.string.no_videos));
                            } else {
                                adapter.setVideos(videos);
                                showContent();
                            }
                        } catch (Exception e) {
                            showError(getString(R.string.error_server));
                        }
                    }
                });
            }

            @Override
            public void onError(final String message) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!isAdded()) return;
                        showError(getString(R.string.error_server));
                    }
                });
            }
        });
    }

    private void openPlayer(Video video) {
        Intent intent = new Intent(getContext(), PlayerActivity.class);
        intent.putExtra("video_id",    video.getId());
        intent.putExtra("video_title", video.getTitle());
        intent.putExtra("video_channel", video.getChannel());
        intent.putExtra("video_views",   video.getViews());
        startActivity(intent);
    }

    // ─── Estados de UI ──────────────────────────

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
        layoutError.setVisibility(View.GONE);
    }

    private void showContent() {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.VISIBLE);
        layoutError.setVisibility(View.GONE);
    }

    private void showError(String message) {
        progressBar.setVisibility(View.GONE);
        recyclerView.setVisibility(View.GONE);
        layoutError.setVisibility(View.VISIBLE);
        txtError.setText(message);
    }
}
