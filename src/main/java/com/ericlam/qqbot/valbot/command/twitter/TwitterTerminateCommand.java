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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ChatCommand(
        name = "termiante",
        alias = {"中止", "中止监听"},
        placeholders = {"<用户ID>"},
        description = "中止监听用户"
)
public class TwitterTerminateCommand implements DiscordGroupCommand, QQGroupCommand {

    @Autowired
    private TwitterService service;

    @Override
    public void executeCommand(GuildMessageChannel channel, MessageCreateEvent event, List<String> args) {
        String username = args.get(0);
        var msg = service.stopListen(username) ? "已中止监听推特用户(" + username + ")。" : "你尚未开始监听此推特用户。";
        channel.createMessage(spec -> spec.setMessageReference(event.getMessage().getId()).setContent(msg)).subscribe();
    }

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        String username = args.get(0);
        var msg = service.stopListen(username) ? "已中止监听推特用户(" + username + ")。" : "你尚未开始监听此推特用户。";
        bot.sendGroupMsg(event.getGroupId(), MsgUtils.builder().text(msg).reply(event.getMessageId()).build(), false);
    }
}
