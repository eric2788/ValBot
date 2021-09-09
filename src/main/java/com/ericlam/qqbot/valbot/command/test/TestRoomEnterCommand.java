package com.ericlam.qqbot.valbot.command.test;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordGroupCommand;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQGroupCommand;
import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
import com.ericlam.qqbot.valbot.redis.wshandle.RoomEnterHandle;
import com.ericlam.qqbot.valbot.service.BLiveTestService;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@ChatCommand(
        name = "enter",
        description = "测试进入直播间广播通知"
)
public class TestRoomEnterCommand implements QQGroupCommand, DiscordGroupCommand {

    @Autowired
    private BLiveTestService testService;

    @Autowired
    private RoomEnterHandle roomEnterHandle;

    @Override
    public void executeCommand(GuildMessageChannel channel, MessageCreateEvent event, List<String> args) {
        BLiveWebSocketData ws = testService.generateInteractWordData();
        try {
            roomEnterHandle.handle(channel, ws.data.room, ws);
        } catch (IOException e) {
            channel.createMessage("测试开播通知时出现错误: "+e.getMessage()).subscribe();
            e.printStackTrace();
        }
    }

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        BLiveWebSocketData ws = testService.generateInteractWordData();
        try {
            roomEnterHandle.handle(bot, event.getGroupId(), ws.data.room, ws);
        } catch (IOException e) {
            bot.sendGroupMsg(event.getGroupId(), "测试开播通知时出现错误: "+e.getMessage(), true);
            e.printStackTrace();
        }
    }
}
