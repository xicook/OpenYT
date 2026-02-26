package com.openyt.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.openyt.app.R;
import com.openyt.app.models.UpdateMessage;
import java.util.ArrayList;
import java.util.List;

public class UpdateAdapter extends RecyclerView.Adapter<UpdateAdapter.UpdateViewHolder> {

    private final Context context;
    private final List<UpdateMessage> messages = new ArrayList<>();

    public UpdateAdapter(Context context) {
        this.context = context;
    }

    public void setMessages(List<UpdateMessage> newMessages) {
        messages.clear();
        messages.addAll(newMessages);
        notifyDataSetChanged();
    }

    @Override
    public UpdateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_update, parent, false);
        return new UpdateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UpdateViewHolder holder, int position) {
        UpdateMessage msg = messages.get(position);
        holder.txtMessage.setText(msg.getText());
        holder.txtTimestamp.setText(msg.getTimestamp());
    }

    @Override
    public int getItemCount() { return messages.size(); }

    static class UpdateViewHolder extends RecyclerView.ViewHolder {
        TextView txtMessage, txtTimestamp;

        UpdateViewHolder(View itemView) {
            super(itemView);
            txtMessage   = itemView.findViewById(R.id.txt_message);
            txtTimestamp = itemView.findViewById(R.id.txt_timestamp);
        }
    }
}
