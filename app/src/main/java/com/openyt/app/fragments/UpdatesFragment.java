package com.openyt.app.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.openyt.app.R;
import com.openyt.app.adapters.UpdateAdapter;
import com.openyt.app.models.UpdateMessage;
import com.openyt.app.network.ApiClient;
import com.openyt.app.utils.JsonParser;

import java.util.List;

/**
 * Fragmento de Notícias e Updates (lê de /opt/app_msg.txt via servidor).
 */
public class UpdatesFragment extends Fragment {

    private RecyclerView recyclerUpdates;
    private ProgressBar progressBar;
    private LinearLayout layoutEmpty;

    private UpdateAdapter adapter;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_updates, container, false);

        recyclerUpdates = view.findViewById(R.id.recycler_updates);
        progressBar     = view.findViewById(R.id.progress_bar);
        layoutEmpty     = view.findViewById(R.id.layout_empty);

        recyclerUpdates.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UpdateAdapter(getContext());
        recyclerUpdates.setAdapter(adapter);

        loadUpdates();
        return view;
    }

    private void loadUpdates() {
        progressBar.setVisibility(View.VISIBLE);
        recyclerUpdates.setVisibility(View.GONE);
        layoutEmpty.setVisibility(View.GONE);

        ApiClient.getInstance().getUpdates(new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(final JsonObject response) {
                mainHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        if (!isAdded()) return;
                        progressBar.setVisibility(View.GONE);

                        try {
                            JsonArray arr = response.getAsJsonArray("messages");
                            List<UpdateMessage> messages = JsonParser.parseUpdateList(arr);

                            if (messages.isEmpty()) {
                                layoutEmpty.setVisibility(View.VISIBLE);
                            } else {
                                adapter.setMessages(messages);
                                recyclerUpdates.setVisibility(View.VISIBLE);
                            }
                        } catch (Exception e) {
                            layoutEmpty.setVisibility(View.VISIBLE);
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
                        progressBar.setVisibility(View.GONE);
                        layoutEmpty.setVisibility(View.VISIBLE);
                    }
                });
            }
        });
    }
}
