package com.ericlam.qqbot.valbot.command.youtube;

import com.ericlam.qqbot.valbot.RequestException;
import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordGroupCommand;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQGroupCommand;
import com.ericlam.qqbot.valbot.service.YoutubeLiveService;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.text.MessageFormat;
import java.util.List;

@Component
@ChatCommand(
        name = "convert",
        alias = {"转换"},
        placeholders = "<用户名>",
        description = "从用户名转换成频道ID"
)
public class YoutubeConvertCommand implements DiscordGroupCommand, QQGroupCommand {

    @Autowired
    private YoutubeLiveService liveService;

    @Override
    public void executeCommand(GuildMessageChannel channel, MessageCreateEvent event, List<String> args) {
        var username = args.get(0);
        liveService.getChannelForUserName(username)
                .flatMap(id -> channel.createMessage(spec -> {
                    spec.setMessageReference(event.getMessage().getId());
                    spec.setContent(MessageFormat.format("用户名 {0} 的频道 ID 是 {1}", username, id));
                }))
                .onErrorResume(ex -> {
                    if (!(ex instanceof RequestException)){
                        ex.printStackTrace();
                    }
                    return channel.createMessage(spec -> {
                        spec.setMessageReference(event.getMessage().getId());
                        spec.setContent("尝试转换时出现错误: " + ex.getMessage());
                    });
                })
                .subscribe();
    }

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
         var username = args.get(0);
         liveService.getChannelForUserName(username)
                 .onErrorResume(ex -> {
                     if (!(ex instanceof RequestException)){
                         ex.printStackTrace();
                     }
                     bot.sendGroupMsg(event.getGroupId(), MsgUtils.builder()
                             .reply(event.getMessageId())
                             .text("尝试转换时出现错误: ")
                             .text(ex.getMessage())
                             .build(), false);
                     return Mono.empty();
                 })
                 .subscribe(id -> {
                     var msg = MessageFormat.format("用户名 {0} 的频道 ID 是 {1}", username, id);
                     bot.sendGroupMsg(event.getGroupId(), MsgUtils.builder()
                             .reply(event.getMessageId())
                             .text(msg)
                             .build(), false);
                 });
    }
}
