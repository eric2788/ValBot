package com.ericlam.qqbot.valbot.dto;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ValBotData implements Serializable {

    public Map<String, Boolean> answers = new HashMap<>();

    public Map<String, String> responses = new HashMap<>();

    public BLiveSettings bLiveSettings = new BLiveSettings();
    public YoutubeSettings youtubeSettings = new YoutubeSettings();

    public CommonSettings settings = new CommonSettings();

    public static class CommonSettings {
        public boolean verboseDelete = false;
        public boolean yearlyCheck = false;
        public boolean verbose = false;
    }

    public static class BLiveSettings {

        public Set<Long> listening = new HashSet<>();
        public Set<Long> highlightUsers = new HashSet<>();

    }

    public static class YoutubeSettings {
        public Set<String> listening = new HashSet<>();
    }

    public static class TwitterSettings {
        public Set<String> listening = new HashSet<>();
    }

    public static class TwitchSettings {
        public Set<String> listening = new HashSet<>();
    }

    @Override
    public String toString() {
        return "ValBotData{" +
                "answers=" + answers +
                ", responses=" + responses +
                '}';
    }
}
