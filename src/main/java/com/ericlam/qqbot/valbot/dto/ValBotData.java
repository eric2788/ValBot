package com.ericlam.qqbot.valbot.dto;

import java.util.HashMap;
import java.util.Map;

public class ValBotData {

    public Map<String, Boolean> answers = new HashMap<>();

    public Map<String, String> responses = new HashMap<>();

    @Override
    public String toString() {
        return "ValBotData{" +
                "answers=" + answers +
                ", responses=" + responses +
                '}';
    }
}
