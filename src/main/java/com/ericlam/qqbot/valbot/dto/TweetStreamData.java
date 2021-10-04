package com.ericlam.qqbot.valbot.dto;

import com.alibaba.fastjson.JSONArray;

import javax.annotation.Nullable;
import java.util.Date;
import java.util.List;

public class TweetStreamData {

    public String created_at;
    public Entities entities;
    @Nullable
    public Entities extended_entities;
    public long favourite_count;
    public long id;
    public String id_str;
    public String lang;
    public long quote_count;
    public long reply_count;
    public long retweet_count;
    public String source;
    public String text;
    public long timestamp_ms;
    public boolean truncated;
    public TweetUser user;
    public boolean is_quote_status;

    public boolean possibly_sensitive;

    @Nullable
    public TweetStreamData retweeted_status;

    @Nullable
    public DeleteData delete;

    public static class DeleteData {
        public Status status;
        public long timestamp_ms;
    }


    public static class Status {
        public long id;
        public String id_str;
        public long user_id;
        public String user_id_str;
    }

    public static class Entities {

        public JSONArray hashtags; // not sure the properties
        public JSONArray symbols; // not sure the properties
        public List<Url> urls;
        public List<UserMention> user_mentions;
        @Nullable // if no images
        public List<Media> media;
    }

    public static class Url {
        @Nullable // url no preview image
        public String display_url;
        public String expanded_url;
        public String url;
    }

    public static class UserMention {
        public long id;
        public String id_str;
        public String name;
        public String screen_name;
    }

    public static class Media {
        public long id;
        public String id_str;
        public String media_url;
        public String media_url_https;
        public String url;
        public String display_url;
        public String expand_url;
        public String type;
    }


    public boolean isDeleteTweet() {
        return delete != null;
    }

    public boolean isRetweet() {
        return retweeted_status != null;
    }

    public enum Command {
        TWEET, RETWEET, DELETE
    }
}
