package com.ericlam.qqbot.valbot.dto;


import java.io.Serializable;

public class SuperChatMessage implements Serializable {

    public long uid;
    public int price;
    public String message;
    public long start_time;

    public String background_color_start;
    public String background_image;
    public String background_color;
    public UserInfo user_info;

    public static class UserInfo {
        public String face;
        public String name_color;
        public String uname;
    }
}
