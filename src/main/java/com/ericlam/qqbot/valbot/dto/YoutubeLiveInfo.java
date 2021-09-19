package com.ericlam.qqbot.valbot.dto;

import javax.annotation.Nullable;
import java.util.Date;

public class YoutubeLiveInfo {

    public static class LiveStatus {

        public static final String LIVE = "live";
        public static final String IDLE = "idle";
        public static final String UPCOMING = "upcoming";

    }

    public String channelId;

    public String channelName;

    public String status;

    @Nullable
    public BroadcastInfo info;


    @Override
    public String toString() {
        return "YoutubeLiveInfo{" +
                "channelId='" + channelId + '\'' +
                ", channelName='" + channelName + '\'' +
                ", status='" + status + '\'' +
                ", info=" + info +
                '}';
    }

    public static class BroadcastInfo {

        @Nullable
        public String cover;

        public String title;

        public String url;

        public Date publishTime;

        public String description;


        @Override
        public String toString() {
            return "BroadcastInfo{" +
                    "cover='" + cover + '\'' +
                    ", title='" + title + '\'' +
                    ", url='" + url + '\'' +
                    ", publishTime=" + publishTime +
                    ", description='" + description + '\'' +
                    '}';
        }
    }

}
