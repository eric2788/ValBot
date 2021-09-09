package com.ericlam.qqbot.valbot.crossplatform.discord;

import com.ericlam.qqbot.valbot.crossplatform.ChatResponse;
import discord4j.core.event.domain.message.MessageCreateEvent;

import javax.annotation.Nullable;

public interface DiscordChatResponse extends ChatResponse {

    @Nullable
    String onDiscordResponse(MessageCreateEvent event);

}
