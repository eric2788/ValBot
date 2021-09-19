package com.ericlam.qqbot.valbot.command.youtube;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordGroupCommand;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQGroupCommand;
import com.ericlam.qqbot.valbot.service.YoutubeLiveService;
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
        alias = {"监听中", "正在监听"},
        description = "获取正在监听的频道 id"
)
public class YoutubeListeningCommand implements DiscordGroupCommand, QQGroupCommand {

    @Autowired
    private YoutubeLiveService youtubeLiveService;

    @Resource(name = "random")
    private Color randomColor;

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        bot.sendGroupMsg(event.getGroupId(), MsgUtils
                .builder()
                .text("正在监听的房间号: "+youtubeLiveService.getChannelListening().toString())
                .reply(event.getMessageId())
                .build(), false);
    }

    @Override
    public void executeCommand(GuildMessageChannel channel, MessageCreateEvent event, List<String> args) {
        var content = youtubeLiveService.getChannelListening().isEmpty() ? "无" : youtubeLiveService.getChannelListening()
                .stream()
                .map(String::valueOf)
                .collect(Collectors.joining("\n"));
        channel.createMessage(spec -> {
            spec.addEmbed(em -> {
                em.setColor(randomColor);
                em.addField("正在监听的房间号: ", content, false);
            });
            spec.setMessageReference(event.getMessage().getId());
        }).subscribe();
    }
}
