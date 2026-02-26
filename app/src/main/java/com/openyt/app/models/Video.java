package com.openyt.app.models;

/**
 * Representa um vídeo retornado pela API do servidor OpenYT.
 */
public class Video {

    private String id;
    private String title;
    private String channel;
    private String channelId;
    private String thumbnail;
    private String duration;
    private long views;
    private long likes;
    private String description;
    private String uploadDate;

    public Video() {}

    public Video(String id, String title, String channel, String thumbnail, String duration, long views) {
        this.id = id;
        this.title = title;
        this.channel = channel;
        this.thumbnail = thumbnail;
        this.duration = duration;
        this.views = views;
    }

    // ─── Getters ────────────────────────────────

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getChannel() { return channel; }
    public String getChannelId() { return channelId; }
    public String getThumbnail() { return thumbnail; }
    public String getDuration() { return duration; }
    public long getViews() { return views; }
    public long getLikes() { return likes; }
    public String getDescription() { return description; }
    public String getUploadDate() { return uploadDate; }

    // ─── Setters ────────────────────────────────

    public void setId(String id) { this.id = id; }
    public void setTitle(String title) { this.title = title; }
    public void setChannel(String channel) { this.channel = channel; }
    public void setChannelId(String channelId) { this.channelId = channelId; }
    public void setThumbnail(String thumbnail) { this.thumbnail = thumbnail; }
    public void setDuration(String duration) { this.duration = duration; }
    public void setViews(long views) { this.views = views; }
    public void setLikes(long likes) { this.likes = likes; }
    public void setDescription(String description) { this.description = description; }
    public void setUploadDate(String uploadDate) { this.uploadDate = uploadDate; }

    /** Formata as visualizações de forma legível */
    public String getFormattedViews() {
        if (views >= 1_000_000) {
            return String.format("%.1fM", views / 1_000_000.0);
        } else if (views >= 1_000) {
            return String.format("%.0fK", views / 1_000.0);
        }
        return String.valueOf(views);
    }
}
