package com.openyt.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
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
 * Fragmento de pesquisa de vídeos.
 */
public class SearchFragment extends Fragment {

    private EditText editSearch;
    private ImageButton btnSearch;
    private RecyclerView recyclerResults;
    private LinearLayout layoutEmpty;
    private TextView txtEmpty;
    private ProgressBar progressBar;

    private VideoAdapter adapter;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_search, container, false);

        editSearch      = view.findViewById(R.id.edit_search);
        btnSearch       = view.findViewById(R.id.btn_do_search);
        recyclerResults = view.findViewById(R.id.recycler_results);
        layoutEmpty     = view.findViewById(R.id.layout_empty);
        txtEmpty        = view.findViewById(R.id.txt_empty);
        progressBar     = view.findViewById(R.id.progress_bar);

        recyclerResults.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new VideoAdapter(getContext(), new VideoAdapter.OnVideoClickListener() {
            @Override
            public void onVideoClick(Video video) {
                openPlayer(video);
            }
        });
        recyclerResults.setAdapter(adapter);

        btnSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                doSearch();
            }
        });

        // Pesquisa ao pressionar Enter no teclado
        editSearch.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    doSearch();
                    return true;
                }
                return false;
            }
        });

        showEmpty(getString(R.string.search_empty));
        return view;
    }

    private void doSearch() {
        String query = editSearch.getText().toString().trim();
        if (query.isEmpty()) {
            showEmpty(getString(R.string.search_empty));
            return;
        }

        // Esconde o teclado
        InputMethodManager imm = (InputMethodManager) getContext()
                .getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        if (imm != null) imm.hideSoftInputFromWindow(editSearch.getWindowToken(), 0);

        showLoading();

        ApiClient.getInstance().search(query, new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(final JsonObject response) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!isAdded()) return;
                        try {
                            JsonArray arr = response.getAsJsonArray("videos");
                            List<Video> videos = JsonParser.parseVideoList(arr);
                            if (videos.isEmpty()) {
                                showEmpty(getString(R.string.no_videos));
                            } else {
                                adapter.setVideos(videos);
                                showResults();
                            }
                        } catch (Exception e) {
                            showEmpty(getString(R.string.error_server));
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
                        showEmpty(getString(R.string.error_server));
                    }
                });
            }
        });
    }

    private void openPlayer(Video video) {
        Intent intent = new Intent(getContext(), PlayerActivity.class);
        intent.putExtra("video_id",      video.getId());
        intent.putExtra("video_title",   video.getTitle());
        intent.putExtra("video_channel", video.getChannel());
        intent.putExtra("video_views",   video.getViews());
        startActivity(intent);
    }

    private void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerResults.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);
    }

    private void showResults() {
        progressBar.setVisibility(View.GONE);
        recyclerResults.setVisibility(View.VISIBLE);
        layoutEmpty.setVisibility(View.GONE);
    }

    private void showEmpty(String msg) {
        progressBar.setVisibility(View.GONE);
        recyclerResults.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.VISIBLE);
        txtEmpty.setText(msg);
    }
}
