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

import java.text.MessageFormat;
import java.util.List;

@Component
@ChatCommand(
        name = "listen",
        description = "启动频道监听",
        alias = {"监听", "启动监听", "启动"},
        placeholders = {"<频道链接>"}
)
public class YoutubeListenCommand implements DiscordGroupCommand, QQGroupCommand {

    @Autowired
    private YoutubeLiveService youtubeLiveService;

    @Override
    public void executeCommand(GuildMessageChannel channel, MessageCreateEvent event, List<String> args) {
        String channelLink = args.get(0);

        youtubeLiveService.getChannel(channelLink)
                .doOnError(ex -> {
                    ex.printStackTrace();
                    channel.createMessage(spec -> {
                        spec.setMessageReference(event.getMessage().getId());
                        spec.setContent("启动监听时出现错误: " + ex.getMessage());
                    }).subscribe();
                })
                .map(channelId -> youtubeLiveService.startListen(channelId))
                .flatMap(b -> channel.createMessage(spec -> {
                    spec.setMessageReference(event.getMessage().getId());
                    spec.setContent(MessageFormat.format(b ? "开始监听频道 {0}" : "频道 {0} 已启动监听", channelLink));
                }))
                .subscribe();
    }

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        String channelLink = args.get(0);
        youtubeLiveService.getChannel(channelLink)
                .doOnError(ex -> {
                    ex.printStackTrace();
                    bot.sendGroupMsg(event.getGroupId(), MsgUtils.builder()
                            .reply(event.getMessageId())
                            .text("启动监听时出现错误: ")
                            .text(ex.getMessage())
                            .build(), false);
                })
                .map(channelId -> youtubeLiveService.startListen(channelId))
                .subscribe(b -> {
                    var msg = MessageFormat.format(b ? "开始监听频道 {0}" : "频道 {0} 已启动监听", channelLink);
                    bot.sendGroupMsg(event.getGroupId(), MsgUtils.builder()
                            .reply(event.getMessageId())
                            .text(msg)
                            .build(), false);
                });
    }
}
