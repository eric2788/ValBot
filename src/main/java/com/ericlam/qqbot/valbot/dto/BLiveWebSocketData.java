package com.ericlam.qqbot.valbot.dto;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;


public class BLiveWebSocketData implements Serializable {

    public static class CommandType {

        public static final String DANMU_MSG = "DANMU_MSG";
        public static final String SEND_GIFT = "SEND_GIFT";
        public static final String GUARD_BUY = "GUARD_BUY";
        public static final String SUPER_CHAT_MESSAGE = "SUPER_CHAT_MESSAGE";
        public static final String LIVE = "LIVE";
        public static final String INTERACT_WORD = "INTERACT_WORD";

        public static final String BOT_TESTING = "BOT_TESTING";

    }

    public String command;
    public BLiveInfo live_info;
    public JSONObject content;

    public static class BLiveInfo {

        public long uid;
        public String title;
        public String name;
        public String cover;
        public long room_id;

        @Override
        public String toString() {
            return "BLiveInfo{" +
                    "uid=" + uid +
                    ", title='" + title + '\'' +
                    ", name='" + name + '\'' +
                    ", cover='" + cover + '\'' +
                    ", room_id=" + room_id +
                    '}';
        }
    }


    @Override
    public String toString() {
        return "BLiveWebSocketData{" +
                "command='" + command + '\'' +
                ", live_info=" + live_info +
                ", content=" + content +
                '}';
    }
}
