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
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@ChatCommand(
        name = "listen",
        alias = {"监听"},
        description = "启动监听用户",
        placeholders = {"<用户ID>"}
)
public class TwitterListenCommand implements DiscordGroupCommand, QQGroupCommand {

    @Autowired
    private TwitterService service;

    @Override
    public void executeCommand(GuildMessageChannel channel, MessageCreateEvent event, List<String> args) {
        String username = args.get(0);
        service.fetchTwitterUser(username)
                .flatMap(res -> {
                    String msg;
                    if (!res.exist){
                        msg = "此推特用户不存在。";
                    }else{
                        msg = service.startListen(username) ? "开始监听推特用户(" + username + ")。" : "该用户(" + username + ")已经启动监听。";
                    }
                    return channel.createMessage(spec -> spec.setMessageReference(event.getMessage().getId()).setContent(msg));
                })
                .onErrorResume(ex -> {
                    ex.printStackTrace();
                    return channel.createMessage(spec ->
                            spec.setContent("监听推特用户 "+username+" 时出现错误: "+ex.getMessage())
                                    .setMessageReference(event.getMessage().getId()));
                })
                .subscribe();
    }

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        String username = args.get(0);
        service.fetchTwitterUser(username)
                .onErrorResume(ex -> {
                    ex.printStackTrace();
                    bot.sendGroupMsg(event.getGroupId(), MsgUtils.builder()
                            .reply(event.getMessageId())
                            .text("监听用户 "+username+" 时出现错误: ")
                            .text(ex.getMessage())
                            .build(), false);
                    return Mono.empty();
                }).subscribe(res -> {
                    String msg;
                    if (!res.exist){
                        msg = "此推特用户不存在。";
                    }else{
                        msg = service.startListen(username) ? "开始监听推特用户(" + username + ")。" : "该用户(" + username + ")已经启动监听。";
                    }
                    bot.sendGroupMsg(event.getGroupId(), MsgUtils.builder().text(msg).reply(event.getMessageId()).build(), false);
                });
    }
}
