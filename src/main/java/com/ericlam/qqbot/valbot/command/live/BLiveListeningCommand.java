package com.ericlam.qqbot.valbot.command.live;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.command.GroupChatCommand;
import com.ericlam.qqbot.valbot.redis.BilibiliLiveService;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ChatCommand(
        name = "list",
        description = "获取正在监听的房间号"
)
public class BLiveListeningCommand implements GroupChatCommand {

    @Autowired
    private BilibiliLiveService liveService;

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        bot.sendGroupMsg(event.getGroupId(), "正在监听的房间号: "+liveService.getLiveRoomListening().toString(), true);
    }
}
