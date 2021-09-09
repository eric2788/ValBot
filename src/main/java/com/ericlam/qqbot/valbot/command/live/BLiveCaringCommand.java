package com.ericlam.qqbot.valbot.command.live;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordGroupCommand;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQGroupCommand;
import com.ericlam.qqbot.valbot.service.ValDataService;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@ChatCommand(
        name = "caring",
        alias = {"正在关注", "关注中", "关注列表"},
        description = "获取高亮用户列表"
)
public class BLiveCaringCommand implements QQGroupCommand, DiscordGroupCommand {

    @Autowired
    private ValDataService dataService;

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        bot.sendGroupMsg(event.getGroupId(),
                MsgUtils.builder()
                        .text("高亮用户列表: "+dataService.getData().bLiveSettings.highlightUsers.toString())
                        .reply(event.getMessageId())
                        .build(), false);
    }

    @Override
    public void executeCommand(MessageChannel channel, MessageCreateEvent event, List<String> args) {
        channel.createMessage(spec -> {
            spec.setMessageReference(event.getMessage().getId());
            spec.addEmbed(em -> {
                em.addField("高亮用户列表",
                        dataService.getData()
                                .bLiveSettings.highlightUsers
                                .stream()
                                .map(String::valueOf)
                                .collect(Collectors.joining("\n")), false);
            });
        }).subscribe();
    }
}
