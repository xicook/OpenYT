package com.openyt.app.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.openyt.app.R;
import com.openyt.app.activities.PlayerActivity;
import com.openyt.app.adapters.VideoAdapter;
import com.openyt.app.models.Video;
import com.openyt.app.network.ApiClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment {

    private RecyclerView recyclerView;
    private VideoAdapter adapter;
    private TextView txtEmpty;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        recyclerView = view.findViewById(R.id.recycler_home);
        txtEmpty     = view.findViewById(R.id.txt_empty);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new VideoAdapter(getContext(), video -> openPlayer(video));
        recyclerView.setAdapter(adapter);

        loadVideos();
        return view;
    }

    private void loadVideos() {
        ApiClient.getInstance().getTrending(new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JsonObject response) {
                mainHandler.post(() -> {
                    if (!isAdded()) return;
                    try {
                        JsonArray arr = response.getAsJsonArray("videos");
                        List<Video> videos = new ArrayList<>();
                        for (int i = 0; i < arr.size(); i++) {
                            JsonObject v = arr.get(i).getAsJsonObject();
                            videos.add(new Video(
                                v.get("id").getAsString(),
                                v.get("title").getAsString(),
                                v.has("channel") && !v.get("channel").isJsonNull() ? v.get("channel").getAsString() : "",
                                v.has("thumbnail") && !v.get("thumbnail").isJsonNull() ? v.get("thumbnail").getAsString() : "",
                                v.has("duration") && !v.get("duration").isJsonNull() ? v.get("duration").getAsString() : "",
                                v.has("views") && !v.get("views").isJsonNull() ? v.get("views").getAsLong() : 0
                            ));
                        }
                        if (videos.isEmpty()) {
                            txtEmpty.setVisibility(View.VISIBLE);
                        } else {
                            txtEmpty.setVisibility(View.GONE);
                            adapter.setVideos(videos);
                        }
                    } catch (Exception e) {
                        txtEmpty.setVisibility(View.VISIBLE);
                    }
                });
            }
            @Override
            public void onError(String message) {
                mainHandler.post(() -> {
                    if (!isAdded()) return;
                    txtEmpty.setVisibility(View.VISIBLE);
                });
            }
        });
    }

    private void openPlayer(Video video) {
        Intent intent = new Intent(getContext(), PlayerActivity.class);
        intent.putExtra("video_id", video.getId());
        intent.putExtra("video_title", video.getTitle());
        intent.putExtra("video_channel", video.getChannel());
        intent.putExtra("video_views", video.getViews());
        startActivity(intent);
    }
}
