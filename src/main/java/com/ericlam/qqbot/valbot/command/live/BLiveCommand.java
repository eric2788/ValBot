package com.ericlam.qqbot.valbot.command.live;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.GroupCommand;


@ChatCommand(
        name = "blive",
        description = "blive 直播间监听指令",
        subCommands = {
                BLiveListenCommand.class,
                BLiveTerminateCommand.class,
                BLiveListeningCommand.class,
                BLiveCareCommand.class,
                BLiveUncareCommand.class,
                BLiveCaringCommand.class,
                BLiveVerboseCommand.class,
                BLiveClearInfoCommand.class
        }
)
public class BLiveCommand implements GroupCommand {
}
