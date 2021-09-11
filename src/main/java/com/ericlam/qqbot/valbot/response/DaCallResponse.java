package com.ericlam.qqbot.valbot.response;

import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordChatResponse;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQChatResponse;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class DaCallResponse implements QQChatResponse, DiscordChatResponse {

    private static final Pattern pattern = Pattern.compile("^.*[给为](.+)打[Cc]all.*$");

    @Nullable
    @Override
    public String onQQResponse(GroupMessageEvent event) {
        Matcher matcher = pattern.matcher(event.getMessage());
        if (!matcher.find()) return null;
        String who = matcher.group(1);
        return ("\\"+who+"/").repeat(5);
    }

    @Nullable
    @Override
    public String onDiscordResponse(MessageCreateEvent event) {
        Matcher matcher = pattern.matcher(event.getMessage().getContent());
        if (!matcher.find()) return null;
        String who = matcher.group(1);
        return ("\\\\"+who+"//").repeat(5);
    }
}
