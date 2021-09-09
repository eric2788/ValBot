package com.ericlam.qqbot.valbot.response;

import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordChatResponse;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQChatResponse;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class RepeatResponse implements QQChatResponse, DiscordChatResponse {

    @Nullable
    @Override
    public String onQQResponse(GroupMessageEvent event) {
        if (!event.getMessage().startsWith("复读")) return null;
        return event.getMessage().replace("复读", "");
    }

    @Nullable
    @Override
    public String onDiscordResponse(MessageCreateEvent event) {
        if (!event.getMessage().getContent().startsWith("复读")) return null;
        return event.getMessage().getContent().replace("复读", "");
    }
}
