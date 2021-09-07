package com.ericlam.qqbot.valbot.command.live;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.command.GroupChatCommand;
import com.ericlam.qqbot.valbot.redis.BilibiliLiveService;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ChatCommand(
        name = "terminate",
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
            bot.sendGroupMsg(event.getGroupId(), "不是有效的房间号", true);
            return;
        }
        liveService.stopListen(roomId);
        bot.sendGroupMsg(event.getGroupId(), "正在请求中止监听直播房间("+roomId+")...", true);
    }
}
