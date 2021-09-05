package com.ericlam.qqbot.valbot.command;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;

import java.util.List;

public interface GroupChatCommand {

    void executeCommand(Bot bot, GroupMessageEvent event, List<String> args);


}
