package com.ericlam.qqbot.valbot.command.live;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.command.GroupChatCommand;
import com.ericlam.qqbot.valbot.service.BilibiliLiveService;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
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
public class BLiveTerminateCommand implements GroupChatCommand {

    @Autowired
    private BilibiliLiveService liveService;

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        long roomId;
        try {
            roomId  = Long.parseLong(args.get(0));
        }catch (NumberFormatException e){
            bot.sendGroupMsg(event.getGroupId(), MsgUtils
                    .builder()
                    .text("不是有效的房间号")
                    .reply(event.getMessageId()).build(), false);
            return;
        }
        if (liveService.stopListen(roomId)){
            bot.sendGroupMsg(event.getGroupId(), MsgUtils
                    .builder()
                    .text("已中止监听直播房间("+roomId+")。")
                    .reply(event.getMessageId())
                    .build(), false);
        }else{
            bot.sendGroupMsg(event.getGroupId(), MsgUtils
                    .builder()
                    .text("你尚未开始监听此直播房间。")
                    .reply(event.getMessageId())
                    .build(), false);
        }

    }
}
