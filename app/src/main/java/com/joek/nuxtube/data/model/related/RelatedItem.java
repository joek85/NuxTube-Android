package com.joek.nuxtube.data.model.related;

public class RelatedItem {

    String videoId, thumbnail, title, viewCounts, publishedAt, lengthText, channelName, channelId, avatarUrl;
    Boolean isLive;

    public RelatedItem(String videoId, String thumbnail, String title, String viewCounts, String publishedAt, String lengthText, String channelName, String channelId, String avatarUrl, Boolean isLive) {
        this.videoId = videoId;
        this.thumbnail = thumbnail;
        this.title = title;
        this.viewCounts = viewCounts;
        this.publishedAt = publishedAt;
        this.lengthText = lengthText;
        this.channelName = channelName;
        this.channelId = channelId;
        this.avatarUrl = avatarUrl;
        this.isLive = isLive;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getTitle() {
        return title;
    }

    public String getViewCounts() {
        return viewCounts;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public String getLengthText() {
        return lengthText;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public Boolean getLive() {
        return isLive;
    }
}
