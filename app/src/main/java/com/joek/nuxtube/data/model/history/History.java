package com.joek.nuxtube.data.model.history;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "history_table")

public class History {
    @PrimaryKey(autoGenerate = true)
    public int id;

    @ColumnInfo(name = "videoId")
    String videoId;
    @ColumnInfo(name = "title")
    String title;
    @ColumnInfo(name = "channelName")
    String channelName;
    @ColumnInfo(name = "channelId")
    String channelId;
    @ColumnInfo(name = "duration")
    String duration;
    @ColumnInfo(name = "viewCounts")
    String viewCounts;
    @ColumnInfo(name = "PublishedAt")
    String publishedAt;
    @ColumnInfo(name = "thumbnail")
    String thumbnail;
    @ColumnInfo(name = "channelThumbnail")
    String channelThumbnail;
    @ColumnInfo(name = "isLive")
    Boolean isLive;

    public History(String videoId, String title, String channelName, String channelId, String duration, String viewCounts, String publishedAt, String thumbnail, String channelThumbnail, Boolean isLive) {
        this.videoId = videoId;
        this.title = title;
        this.channelName = channelName;
        this.channelId = channelId;
        this.duration = duration;
        this.viewCounts = viewCounts;
        this.publishedAt = publishedAt;
        this.thumbnail = thumbnail;
        this.channelThumbnail = channelThumbnail;
        this.isLive = isLive;
    }

    public String getVideoId() {
        return videoId;
    }

    public String getTitle() {
        return title;
    }

    public String getChannelName() {
        return channelName;
    }

    public String getChannelId() {
        return channelId;
    }

    public String getDuration() {
        return duration;
    }

    public String getViewCounts() {
        return viewCounts;
    }

    public String getPublishedAt() {
        return publishedAt;
    }

    public String getThumbnail() {
        return thumbnail;
    }

    public String getChannelThumbnail() {
        return channelThumbnail;
    }

    public Boolean getIsLive() {
        return isLive;
    }

    public int getId() {
        return id;
    }
}
