package com.openyt.app.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.openyt.app.R;
import com.openyt.app.models.Video;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter para a lista de vídeos (Home e Search).
 */
public class VideoAdapter extends RecyclerView.Adapter<VideoAdapter.VideoViewHolder> {

    public interface OnVideoClickListener {
        void onVideoClick(Video video);
    }

    private List<Video> videos = new ArrayList<>();
    private final Context context;
    private final OnVideoClickListener listener;

    public VideoAdapter(Context context, OnVideoClickListener listener) {
        this.context = context;
        this.listener = listener;
    }

    public void setVideos(List<Video> videos) {
        this.videos = videos;
        notifyDataSetChanged();
    }

    public void addVideos(List<Video> newVideos) {
        int start = this.videos.size();
        this.videos.addAll(newVideos);
        notifyItemRangeInserted(start, newVideos.size());
    }

    public void clear() {
        videos.clear();
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
        holder.bind(video);
    }

    @Override
    public int getItemCount() {
        return videos.size();
    }

    class VideoViewHolder extends RecyclerView.ViewHolder {

        private final ImageView imgThumbnail;
        private final ImageView imgAvatar;
        private final TextView txtTitle;
        private final TextView txtChannel;
        private final TextView txtViews;
        private final TextView txtDuration;

        VideoViewHolder(View itemView) {
            super(itemView);
            imgThumbnail = itemView.findViewById(R.id.img_thumbnail);
            imgAvatar    = itemView.findViewById(R.id.img_avatar);
            txtTitle     = itemView.findViewById(R.id.txt_title);
            txtChannel   = itemView.findViewById(R.id.txt_channel);
            txtViews     = itemView.findViewById(R.id.txt_views);
            txtDuration  = itemView.findViewById(R.id.txt_duration);
        }

        void bind(final Video video) {
            txtTitle.setText(video.getTitle());
            txtChannel.setText(video.getChannel());
            txtDuration.setText(video.getDuration());

            // Visualizações formatadas
            String viewsText = context.getString(R.string.views, video.getFormattedViews());
            txtViews.setText(viewsText);

            // Carrega thumbnail com Picasso
            if (video.getThumbnail() != null && !video.getThumbnail().isEmpty()) {
                Picasso.get()
                        .load(video.getThumbnail())
                        .placeholder(R.color.textSecondary)
                        .error(R.color.textSecondary)
                        .into(imgThumbnail);
            }

            // Clique no card
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (listener != null) {
                        listener.onVideoClick(video);
                    }
                }
            });
        }
    }
}
