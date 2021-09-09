package com.ericlam.qqbot.valbot.crossplatform.discord;

import com.ericlam.qqbot.valbot.crossplatform.GroupCommand;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.MessageChannel;

import java.util.List;

public interface DiscordGroupCommand extends GroupCommand {

    void executeCommand(MessageChannel channel, MessageCreateEvent event, List<String> args);

}
