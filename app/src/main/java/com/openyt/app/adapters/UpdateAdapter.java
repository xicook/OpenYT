package com.openyt.app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.openyt.app.R;
import com.openyt.app.models.UpdateMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter para a lista de mensagens de update.
 */
public class UpdateAdapter extends RecyclerView.Adapter<UpdateAdapter.UpdateViewHolder> {

    private List<UpdateMessage> messages = new ArrayList<>();
    private final Context context;

    public UpdateAdapter(Context context) {
        this.context = context;
    }

    public void setMessages(List<UpdateMessage> messages) {
        this.messages = messages;
        notifyDataSetChanged();
    }

    @Override
    public UpdateViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_update, parent, false);
        return new UpdateViewHolder(view);
    }

    @Override
    public void onBindViewHolder(UpdateViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    class UpdateViewHolder extends RecyclerView.ViewHolder {

        private final TextView txtMessage;
        private final TextView txtDate;

        UpdateViewHolder(View itemView) {
            super(itemView);
            txtMessage = itemView.findViewById(R.id.txt_message);
            txtDate    = itemView.findViewById(R.id.txt_date);
        }

        void bind(UpdateMessage msg) {
            txtMessage.setText(msg.getText());
            txtDate.setText(msg.getFormattedDate());
        }
    }
}
