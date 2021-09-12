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

    public CommonSettings settings = new CommonSettings();

    public static class CommonSettings {
        public boolean verboseDelete = false;
        public boolean yearlyCheck = false;
    }

    public static class BLiveSettings {

        public Set<Long> listening = new HashSet<>();
        public Set<Long> highlightUsers = new HashSet<>();
        public boolean verbose = true;

    }

    @Override
    public String toString() {
        return "ValBotData{" +
                "answers=" + answers +
                ", responses=" + responses +
                '}';
    }
}
