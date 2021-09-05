package com.ericlam.qqbot.valbot.response.group;

import com.ericlam.qqbot.valbot.response.GroupChatResponse;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DaCallResponse implements GroupChatResponse {

    private static final Pattern pattern = Pattern.compile("^.*给(.+)打[Cc]all.*$");

    @Nullable
    @Override
    public String onResponse(GroupMessageEvent event) {
        Matcher matcher = pattern.matcher(event.getMessage());
        if (!matcher.find()) return null;
        String who = matcher.group(1);
        return ("\\"+who+"/").repeat(5);
    }
}
