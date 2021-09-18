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

import java.text.MessageFormat;
import java.util.List;

@Component
@ChatCommand(
        name = "clearinfo",
        description = "清除房间资讯快取",
        placeholders = {"[roomId]"},
        alias = {"清除快取"}
)
public class BLiveClearInfoCommand implements QQGroupCommand, DiscordGroupCommand {

    @Autowired
    private BilibiliLiveService liveService;

    @Override
    public void executeCommand(GuildMessageChannel channel, MessageCreateEvent event, List<String> args) {
        long roomId;
        try {
            roomId = Long.parseLong(args.size() > 0 ? args.get(0) : "-1");
        } catch (NumberFormatException e) {
            channel.createMessage(spec -> spec.setContent("不是有效的房间号").setMessageReference(event.getMessage().getId())).subscribe();
            return;
        }

        var msg = liveService.clearErrorCache(roomId) ? MessageFormat.format("已清除 {0} 房间的资讯快取。", roomId == -1 ? "所有" : roomId) : "没有此房间的资讯快取。";

        channel.createMessage(spec ->
                        spec.setContent(msg).setMessageReference(event.getMessage().getId()))
                .subscribe();
    }

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        long roomId;
        try {
            roomId = Long.parseLong(args.size() > 0 ? args.get(0) : "-1");
        } catch (NumberFormatException e) {
            bot.sendGroupMsg(event.getGroupId(), MsgUtils
                    .builder()
                    .text("不是有效的房间号")
                    .reply(event.getMessageId())
                    .build(), false);
            return;
        }

        var msg = liveService.clearErrorCache(roomId) ? MessageFormat.format("已清除 {0} 房间的资讯快取。", roomId == -1 ? "所有" : roomId) : "没有此房间的资讯快取。";

        bot.sendGroupMsg(event.getGroupId(), MsgUtils
                .builder()
                .text(msg)
                .reply(event.getMessageId())
                .build(), false);

    }
}
