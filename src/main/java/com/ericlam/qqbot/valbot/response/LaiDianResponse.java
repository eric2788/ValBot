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
public class LaiDianResponse implements QQChatResponse, DiscordChatResponse {

    private static final Pattern COME_ON_PATTERN = Pattern.compile(".*[来发]点(.+).*");

    private static final String RESPONSE = "《{0}》";

    @Nullable
    @Override
    public String onDiscordResponse(MessageCreateEvent event) {
        Matcher matcher = COME_ON_PATTERN.matcher(event.getMessage().getContent());
        if (!matcher.find()) return null;
        return MessageFormat.format(RESPONSE, matcher.group(1));
    }

    @Nullable
    @Override
    public String onQQResponse(GroupMessageEvent event) {
        Matcher matcher = COME_ON_PATTERN.matcher(event.getMessage());
        if (!matcher.find()) return null;
        return MessageFormat.format(RESPONSE, matcher.group(1));
    }
}
