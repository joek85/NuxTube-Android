package com.joek.nuxtube.data.model.search;

public class SearchItem {
    String videoId, imageUrl, title, viewCounts, publishedAt, lengthText, channelText, supportingText, avatarUrl;
    Boolean isLive;
    public SearchItem(String videoId, String imageUrl, String title, String viewCounts, String publishedAt, String lengthText, String channelText, String supportingText, String avatarUrl, Boolean isLive) {
        this.videoId = videoId;
        this.imageUrl = imageUrl;
        this.title = title;
        this.viewCounts = viewCounts;
        this.publishedAt = publishedAt;
        this.lengthText = lengthText;
        this.channelText = channelText;
        this.supportingText = supportingText;
        this.avatarUrl = avatarUrl;
        this.isLive = isLive;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public String getTitle() {
        return title;
    }

    public String getSupportingText() {
        return supportingText;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public String getChannelText() {
        return channelText;
    }

    public String getLengthText() {
        return lengthText;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public String getViewCounts() {
        return viewCounts;
    }

    public Boolean getLive() {
        return isLive;
    }
}
