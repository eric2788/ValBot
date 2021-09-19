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

import java.util.List;

@Component
@ChatCommand(
        name = "terminate",
        description = "中止监听频道",
        alias = {"中止", "取消"},
        placeholders = {"<频道链接>"}
)
public class YoutubeTerminateCommand implements DiscordGroupCommand, QQGroupCommand {

    @Autowired
    private YoutubeLiveService youtubeLiveService;

    @Override
    public void executeCommand(GuildMessageChannel channel, MessageCreateEvent event, List<String> args) {
        String link = args.get(0);
        youtubeLiveService.getChannel(link)
                .map(channelId -> youtubeLiveService.stopListen(channelId))
                .flatMap(result -> {
                    String msg = result ? "已中止监听频道( " + link + " )。" : "你尚未开始监听此频道。";
                    return channel.createMessage(spec -> spec.setContent(msg).setMessageReference(event.getMessage().getId()));
                })
                .onErrorResume(ex -> {
                    if (!(ex instanceof RequestException)){
                        ex.printStackTrace();
                    }
                    return channel.createMessage(spec -> {
                        spec.setMessageReference(event.getMessage().getId());
                        spec.setContent("中止监听时出现错误: " + ex.getMessage());
                    });
                })
                .subscribe();
    }

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        String link = args.get(0);
        youtubeLiveService.getChannel(link)
                .onErrorResume(ex ->{
                    if (!(ex instanceof RequestException)){
                        ex.printStackTrace();
                    }
                    bot.sendGroupMsg(event.getGroupId(), MsgUtils.builder()
                            .reply(event.getMessageId())
                            .text("中止监听时出现错误: ")
                            .text(ex.getMessage())
                            .build(), false);

                    return Mono.empty();
                })
                .map(channelId -> youtubeLiveService.stopListen(channelId))
                .subscribe(result -> {
                    String msg = result ? "已中止监听频道( " + link + " )。" : "你尚未开始监听此频道。";
                    bot.sendGroupMsg(event.getGroupId(), MsgUtils
                            .builder()
                            .text(msg)
                            .reply(event.getMessageId())
                            .build(), false);
                });
    }
}
