package com.ericlam.qqbot.valbot.crossplatform.discord;

import com.ericlam.qqbot.valbot.crossplatform.MessageEventSource;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.MessageChannel;

public record DiscordMessageEventSource(MessageChannel channel, MessageCreateEvent event) implements MessageEventSource {
}
