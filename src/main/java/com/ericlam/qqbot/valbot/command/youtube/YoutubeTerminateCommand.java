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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@ChatCommand(
        name = "terminate",
        description = "中止监听频道",
        alias = {"中止", "取消"},
        placeholders = {"<频道ID 或 用户名>"}
)
public class YoutubeTerminateCommand implements DiscordGroupCommand, QQGroupCommand {

    @Autowired
    private YoutubeLiveService youtubeLiveService;

    @Override
    public void executeCommand(GuildMessageChannel channel, MessageCreateEvent event, List<String> args) {
        String id = args.get(0);
        Mono<String> channelIdMono;
        if (!id.startsWith("UC")){
            channelIdMono = youtubeLiveService.getChannelForUserName(id);
        }else{
            channelIdMono = Mono.just(id);
        }
        channelIdMono
                .doOnError(ex -> {
                    ex.printStackTrace();
                    channel.createMessage(spec -> {
                        spec.setMessageReference(event.getMessage().getId());
                        spec.setContent("中止监听时出现错误: " + ex.getMessage());
                    }).subscribe();
                })
                .map(channelId -> youtubeLiveService.stopListen(channelId))
                .flatMap(result -> {
                    String msg = result ? "已中止监听直播房间(" + id + ")。" : "你尚未开始监听此直播房间。";
                    return channel.createMessage(spec -> spec.setContent(msg).setMessageReference(event.getMessage().getId()));
                })
                .subscribe();
    }

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        String id = args.get(0);
        Mono<String> channelIdMono;
        if (!id.startsWith("UC")){
            channelIdMono = youtubeLiveService.getChannelForUserName(id);
        }else{
            channelIdMono = Mono.just(id);
        }
        channelIdMono
                .doOnError(ex ->{
                    ex.printStackTrace();
                    bot.sendGroupMsg(event.getGroupId(), MsgUtils.builder()
                            .reply(event.getMessageId())
                            .text("中止监听时出现错误: ")
                            .text(ex.getMessage())
                            .build(), false);
                })
                .map(channelId -> youtubeLiveService.stopListen(channelId))
                .subscribe(result -> {
                    String msg = result ? "已中止监听直播房间(" + id + ")。" : "你尚未开始监听此直播房间。";
                    bot.sendGroupMsg(event.getGroupId(), MsgUtils
                            .builder()
                            .text(msg)
                            .reply(event.getMessageId())
                            .build(), false);
                });
    }
}
