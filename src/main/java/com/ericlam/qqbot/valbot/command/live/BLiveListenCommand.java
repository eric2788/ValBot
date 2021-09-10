package com.ericlam.qqbot.valbot.command.live;

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
        name = "listen",
        description = "监听",
        placeholders = {"<房间号>"}
)
public class BLiveListenCommand implements QQGroupCommand, DiscordGroupCommand {


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
                    .reply(event.getMessageId())
                    .build(), false);
            return;
        }
        liveService.getRoomInfo(roomId).whenComplete((info, ex) -> {

            if (ex != null) {
                ex.printStackTrace();
                info.msg = ex.getMessage();
            }

            if (info.code == 1){
                bot.sendGroupMsg(event.getGroupId(), "房间号无效: "+info.msg, true);
                return;
            }

            if (liveService.startListen(roomId)) {
                bot.sendGroupMsg(event.getGroupId(), MsgUtils.builder()
                        .text("开始监听直播房间(" + roomId + ")。")
                        .reply(event.getMessageId())
                        .build(), false);
            } else {
                bot.sendGroupMsg(event.getGroupId(), MsgUtils
                        .builder()
                        .text("该直播间(" + roomId + ")已经启动监听。")
                        .reply(event.getMessageId())
                        .build(), false);
            }

        });
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
        liveService.getRoomInfo(roomId).whenComplete((info, ex) -> {

            if (ex != null) {
                ex.printStackTrace();
                info.msg = ex.getMessage();
            }

            if (info.code == 1){
                channel.createMessage(spec ->
                        spec.setContent("房间号无效: "+info.msg)
                                .setMessageReference(event.getMessage().getId())).subscribe();
                return;
            }

            if (liveService.startListen(roomId)) {
                channel.createMessage(spec ->
                        spec.setContent("开始监听直播房间(" + roomId + ")。")
                                .setMessageReference(event.getMessage().getId())).subscribe();
            } else {
                channel.createMessage(spec ->
                        spec.setContent("该直播间(" + roomId + ")已经启动监听。")
                                .setMessageReference(event.getMessage().getId())).subscribe();
            }

        });

    }
}
