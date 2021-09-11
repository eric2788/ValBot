package com.ericlam.qqbot.valbot.command;

import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordGroupCommand;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQGroupCommand;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ChatCommand(
        name = "voice",
        alias = {"语音"},
        description = "发送群语音指令",
        placeholders = "<讯息>"
)
public class VoiceCommand implements QQGroupCommand, DiscordGroupCommand {


    @Override
    public void executeCommand(GuildMessageChannel channel, MessageCreateEvent event, List<String> args) {
        String speak = String.join(" ", args);
        channel.createMessage(spec -> {
            spec.setContent(speak);
            spec.setTts(true);
        }).subscribe();
    }

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        String speak = String.join(" ", args);
        String cq = MsgUtils.builder().tts(speak).build();
        bot.sendGroupMsg(event.getGroupId(), cq, false);
    }
}
