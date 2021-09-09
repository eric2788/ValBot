package com.ericlam.qqbot.valbot.command;

import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordGroupCommand;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQGroupCommand;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ChatCommand(
        name = "say",
        description = "说话指令",
        alias = {"speak", "说话", "说", "复读"},
        placeholders = "<讯息>"
)
public class SpeakCommand implements QQGroupCommand, DiscordGroupCommand {

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        String speak = String.join(" ", args);
        bot.sendGroupMsg(event.getGroupId(), speak, true);
    }

    @Override
    public void executeCommand(GuildMessageChannel channel, MessageCreateEvent event, List<String> args) {
        String speak = String.join(" ", args);
        channel.createMessage(speak).subscribe();
    }
}
