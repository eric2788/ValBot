package com.ericlam.qqbot.valbot.command.twitter;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordGroupCommand;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQGroupCommand;
import com.ericlam.qqbot.valbot.service.TwitterService;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.rest.util.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Collectors;

@Component
@ChatCommand(
        name = "listening",
        description = "获取监听列表",
        alias = {"正在监听", "监听列表"}
)
public class TwitterListeningCommand implements DiscordGroupCommand, QQGroupCommand {

    @Autowired
    private TwitterService service;

    @Resource(name = "random")
    private Color randomColor;

    @Override
    public void executeCommand(GuildMessageChannel channel, MessageCreateEvent event, List<String> args) {
        var content = service.getLiveRoomListening().isEmpty() ? "无" : service.getLiveRoomListening()
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining("\n"));
        channel.createMessage(spec -> {
            spec.addEmbed(em -> {
                em.setColor(randomColor);
                em.addField("正在监听的推特用户: ", content, false);
            });
            spec.setMessageReference(event.getMessage().getId());
        }).subscribe();
    }

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        bot.sendGroupMsg(event.getGroupId(), MsgUtils
                .builder()
                .text("正在监听的推特用户: "+service.getLiveRoomListening().toString())
                .reply(event.getMessageId())
                .build(), false);
    }
}
