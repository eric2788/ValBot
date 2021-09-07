package com.ericlam.qqbot.valbot.command.live;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.command.GroupChatCommand;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;

import java.util.List;


@ChatCommand(
        name = "blive",
        description = "blive 直播间监听指令",
        subCommands = {
                BLiveListenCommand.class,
                BLiveTerminateCommand.class,
                BLiveListeningCommand.class
        }
)
public class BLiveCommand implements GroupChatCommand {

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
    }
}
