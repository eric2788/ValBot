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


    }

    public String command;
    public BLiveData data;

    public static class BLiveData {

        public long uid;
        public String title;
        public String name;
        public String cover;
        public long room;
        public long real_room;
        public JSONObject content;

        @Override
        public String toString() {
            return "BLiveData{" +
                    "uid=" + uid +
                    ", title='" + title + '\'' +
                    ", name='" + name + '\'' +
                    ", cover='" + cover + '\'' +
                    ", room=" + room +
                    ", real_room=" + real_room +
                    ", content=" + content +
                    '}';
        }
    }


    @Override
    public String toString() {
        return "BLiveWebSocketData{" +
                "command='" + command + '\'' +
                ", data=" + data +
                '}';
    }
}
