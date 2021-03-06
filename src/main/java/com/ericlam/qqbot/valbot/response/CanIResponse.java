package com.ericlam.qqbot.valbot.response;

import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordChatResponse;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQChatResponse;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class CanIResponse implements QQChatResponse, DiscordChatResponse {


    private static final Pattern CAN_PATTERN = Pattern.compile(".*(.)不(.).*");

    private static final String NO = "不{0}, 爬";
    private static final String YES = "{0}";


    @Nullable
    @Override
    public String onDiscordResponse(MessageCreateEvent event) {
        Matcher matcher = CAN_PATTERN.matcher(event.getMessage().getContent());
        if (!matcher.find()) return null;
        String first = matcher.group(1);
        String second = matcher.group(2);
        if (!first.equals(second)) return null;
        return MessageFormat.format(NO, first);
    }

    @Nullable
    @Override
    public String onQQResponse(GroupMessageEvent event) {
        Matcher matcher = CAN_PATTERN.matcher(event.getMessage());
        if (!matcher.find()) return null;
        String first = matcher.group(1);
        String second = matcher.group(2);
        if (!first.equals(second)) return null;
        return MessageFormat.format(NO, first);
    }
}
