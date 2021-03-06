package com.ericlam.qqbot.valbot.command.blive;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordGroupCommand;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQGroupCommand;
import com.ericlam.qqbot.valbot.service.BilibiliLiveService;
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
        name = "terminate",
        alias = {"中止监听", "取消监听"},
        description = "中止监听",
        placeholders = {"房间号"}
)
public class BLiveTerminateCommand implements QQGroupCommand, DiscordGroupCommand {

    @Autowired
    private BilibiliLiveService liveService;

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        long roomId;
        try {
            roomId = Long.parseLong(args.get(0));
        } catch (NumberFormatException e) {
            bot.sendGroupMsg(event.getGroupId(), MsgUtils
                    .builder()
                    .text("不是有效的房间号")
                    .reply(event.getMessageId()).build(), false);
            return;
        }
        var msg = liveService.stopListen(roomId) ? "已中止监听直播房间(" + roomId + ")。" : "你尚未开始监听此直播房间。";
        bot.sendGroupMsg(event.getGroupId(), MsgUtils
                .builder()
                .text(msg)
                .reply(event.getMessageId())
                .build(), false);
    }

    @Override
    public void executeCommand(GuildMessageChannel channel, MessageCreateEvent event, List<String> args) {
        long roomId;
        try {
            roomId = Long.parseLong(args.get(0));
        } catch (NumberFormatException e) {
            channel.createMessage(spec -> spec.setContent("不是有效的房间号").setMessageReference(event.getMessage().getId())).subscribe();
            return;
        }
        String msg = liveService.stopListen(roomId) ? "已中止监听直播房间(" + roomId + ")。" : "你尚未开始监听此直播房间。";
        channel.createMessage(spec -> spec.setContent(msg).setMessageReference(event.getMessage().getId())).subscribe();
    }
}
