package com.ericlam.qqbot.valbot.response;

import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordChatResponse;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQChatResponse;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class CanIResponse implements QQChatResponse, DiscordChatResponse {


    private static final Pattern CAN_PATTERN = Pattern.compile(".*能不能.*");

    private static final String NO = "不能";
    private static final String YES = "能";


    @Nullable
    @Override
    public String onDiscordResponse(MessageCreateEvent event) {
        if (!CAN_PATTERN.matcher(event.getMessage().getContent()).find()) return null;
        return NO;
    }

    @Nullable
    @Override
    public String onQQResponse(GroupMessageEvent event) {
        if (!CAN_PATTERN.matcher(event.getMessage()).find()) return null;
        return NO;
    }
}
