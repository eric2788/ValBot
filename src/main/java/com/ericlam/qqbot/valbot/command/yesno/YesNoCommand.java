package com.ericlam.qqbot.valbot.command.yesno;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.command.GroupChatCommand;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;

import java.util.List;

@ChatCommand(
        name = "yesno",
        description = "YesNo指令",
        subCommands = {
                YesNoSetCommand.class,
                YesNoCheckCommand.class,
                YesNoRemoveCommand.class
        }
)
public class YesNoCommand implements GroupChatCommand {

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
    }

}
