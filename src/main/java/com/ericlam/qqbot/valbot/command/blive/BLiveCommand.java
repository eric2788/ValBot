package com.ericlam.qqbot.valbot.command.blive;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.GroupCommand;


@ChatCommand(
        name = "blive",
        alias = {"b站", "b站直播"},
        description = "blive 直播间监听指令",
        subCommands = {
                BLiveListenCommand.class,
                BLiveTerminateCommand.class,
                BLiveListeningCommand.class,
                BLiveCareCommand.class,
                BLiveUncareCommand.class,
                BLiveCaringCommand.class,
                BLiveClearInfoCommand.class
        }
)
public class BLiveCommand implements GroupCommand {
}
