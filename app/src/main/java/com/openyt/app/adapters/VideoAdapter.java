package com.openyt.app.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import com.openyt.app.R;
import com.openyt.app.models.Video;
import com.squareup.picasso.Picasso;
import java.util.ArrayList;
import java.util.List;

public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    public interface OnVideoClickListener {
        void onVideoClick(Video video);
    }

    private final Context context;
    private final List<Video> videos = new ArrayList<>();
    private final OnVideoClickListener listener;

    public VideoAdapter(Context context, OnVideoClickListener listener) {
        this.context  = context;
        this.listener = listener;
    }

    public void setVideos(List<Video> newVideos) {
        videos.clear();
        videos.addAll(newVideos);
        notifyDataSetChanged();
    }

    @Override
    public VideoViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_video, parent, false);
        return new VideoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(VideoViewHolder holder, int position) {
        Video video = videos.get(position);
        holder.txtTitle.setText(video.getTitle());
        holder.txtChannel.setText(video.getChannel());
        holder.txtDuration.setText(video.getDuration());
        if (video.getThumbnail() != null && !video.getThumbnail().isEmpty()) {
            Picasso.get().load(video.getThumbnail()).into(holder.imgThumbnail);
        }
        holder.itemView.setOnClickListener(v -> listener.onVideoClick(video));
    }

    @Override
    public int getItemCount() { return videos.size(); }

    static class VideoViewHolder extends RecyclerView.ViewHolder {
        ImageView imgThumbnail;
        TextView txtTitle, txtChannel, txtDuration;

        VideoViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.img_thumbnail);
            txtTitle     = itemView.findViewById(R.id.txt_title);
            txtChannel   = itemView.findViewById(R.id.txt_channel);
            txtDuration  = itemView.findViewById(R.id.txt_duration);
        }
    }
}
