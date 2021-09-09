package com.ericlam.qqbot.valbot.command.yesno;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.GroupCommand;

@ChatCommand(
        name = "yesno",
        description = "YesNo指令",
        subCommands = {
                YesNoSetCommand.class,
                YesNoCheckCommand.class,
                YesNoRemoveCommand.class
        }
)
public class YesNoCommand implements GroupCommand {
}
