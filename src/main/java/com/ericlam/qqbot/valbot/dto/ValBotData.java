package com.ericlam.qqbot.valbot.dto;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class ValBotData {

    public Map<String, Boolean> answers = new HashMap<>();

    public Map<String, String> responses = new HashMap<>();

    public BLiveSettings bLiveSettings = new BLiveSettings();

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
