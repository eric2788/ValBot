package com.ericlam.qqbot.valbot.command.response;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordGroupCommand;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQGroupCommand;
import com.ericlam.qqbot.valbot.service.ValDataService;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ChatCommand(
        name = "remove",
        description = "移除回应",
        placeholders = {"<文字>"}
)
public class CustomResponseRemoveCommand implements QQGroupCommand, DiscordGroupCommand {

    @Autowired
    private ValDataService dataService;

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        boolean result = dataService.getData().responses.remove(args.get(0)) != null;
        String msg = result ? "移除成功" : "找不到这个文字";
        bot.sendGroupMsg(event.getGroupId(), MsgUtils
                .builder()
                .text(msg)
                .reply(event.getMessageId()).build(), false);
    }

    @Override
    public void executeCommand(GuildMessageChannel channel, MessageCreateEvent event, List<String> args) {
        boolean result = dataService.getData().responses.remove(args.get(0)) != null;
        String msg = result ? "移除成功" : "找不到这个文字";
        channel.createMessage(spec -> spec.setContent(msg).setMessageReference(event.getMessage().getId())).subscribe();
    }
}
