package com.openyt.app.fragments;

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
import com.openyt.app.adapters.UpdateAdapter;
import com.openyt.app.models.UpdateMessage;
import com.openyt.app.network.ApiClient;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.List;

public class UpdatesFragment extends Fragment {

    private RecyclerView recyclerUpdates;
    private UpdateAdapter adapter;
    private TextView txtEmpty;
    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_updates, container, false);
        recyclerUpdates = view.findViewById(R.id.recycler_updates);
        txtEmpty        = view.findViewById(R.id.txt_empty);

        recyclerUpdates.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new UpdateAdapter(getContext());
        recyclerUpdates.setAdapter(adapter);

        loadUpdates();
        return view;
    }

    private void loadUpdates() {
        ApiClient.getInstance().getUpdates(new ApiClient.ApiCallback() {
            @Override
            public void onSuccess(JsonObject response) {
                mainHandler.post(() -> {
                    if (!isAdded()) return;
                    try {
                        JsonArray arr = response.getAsJsonArray("messages");
                        List<UpdateMessage> msgs = new ArrayList<>();
                        for (int i = 0; i < arr.size(); i++) {
                            JsonObject m = arr.get(i).getAsJsonObject();
                            msgs.add(new UpdateMessage(
                                m.get("id").getAsInt(),
                                m.get("text").getAsString(),
                                m.has("timestamp") ? m.get("timestamp").getAsString() : ""
                            ));
                        }
                        if (msgs.isEmpty()) {
                            txtEmpty.setVisibility(View.VISIBLE);
                        } else {
                            txtEmpty.setVisibility(View.GONE);
                            adapter.setMessages(msgs);
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
}
