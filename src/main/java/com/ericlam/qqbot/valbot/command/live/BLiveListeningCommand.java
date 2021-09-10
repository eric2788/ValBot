package com.ericlam.qqbot.valbot.command.live;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordGroupCommand;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQGroupCommand;
import com.ericlam.qqbot.valbot.service.BilibiliLiveService;
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
        alias = {"正在监听", "监听列表"},
        description = "获取正在监听的房间号"
)
public class BLiveListeningCommand implements QQGroupCommand, DiscordGroupCommand {

    @Autowired
    private BilibiliLiveService liveService;

    @Resource(name = "random")
    private Color randomColor;

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        bot.sendGroupMsg(event.getGroupId(), MsgUtils
                .builder()
                .text("正在监听的房间号: "+liveService.getLiveRoomListening().toString())
                .reply(event.getMessageId())
                .build(), false);
    }

    @Override
    public void executeCommand(GuildMessageChannel channel, MessageCreateEvent event, List<String> args) {
        var content = liveService.getLiveRoomListening().isEmpty() ? "无" : liveService.getLiveRoomListening()
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
