package com.ericlam.qqbot.valbot.command.live;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.command.GroupChatCommand;
import com.ericlam.qqbot.valbot.service.ValDataService;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ChatCommand(
        name = "caring",
        alias = {"正在关注", "关注中", "关注列表"},
        description = "获取高亮用户列表"
)
public class BLiveCaringCommand implements GroupChatCommand {

    @Autowired
    private ValDataService dataService;

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        bot.sendGroupMsg(event.getGroupId(),
                MsgUtils.builder()
                        .text("高亮用户列表: "+dataService.getData().bLiveSettings.highlightUsers.toString())
                        .reply(event.getMessageId())
                        .build(), false);
    }
}
