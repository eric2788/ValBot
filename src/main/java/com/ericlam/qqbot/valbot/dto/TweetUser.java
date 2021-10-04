package com.ericlam.qqbot.valbot.dto;

import com.alibaba.fastjson.annotation.JSONField;

import javax.annotation.Nullable;
import java.util.Date;

public class TweetUser {
    public String created_at;
    public boolean default_profile;
    @Nullable
    public String url;
    public boolean default_profile_image;
    public String description;
    public long favourites_count;
    public long followers_count;
    public long friends_count;
    public long id;
    public String id_str;
    public long listed_count;
    public String location;
    public String name;
    public String profile_background_color;
    public String profile_background_image_url;
    public String profile_background_image_url_https;
    public boolean profile_background_tile;
    public String profile_banner_url;
    public String profile_image_url;
    public String profile_image_url_https;
    public String profile_link_color;
    public String profile_sidebar_border_color;
    public String profile_sidebar_fill_color;
    public String profile_text_color;
    public boolean profile_use_background_image;
    @JSONField(name = "protected") // protected is a keyword in java
    public boolean is_protected;
    public String screen_name;
    public long statuses_count;
    public boolean verified;
}
