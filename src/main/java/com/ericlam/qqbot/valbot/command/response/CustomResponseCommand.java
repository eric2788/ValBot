package com.ericlam.qqbot.valbot.command.response;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.GroupCommand;

@ChatCommand(
        name = "res",
        description = "自定义回应",
        subCommands = {
                CustomResponseSetCommand.class,
                CustomResponseRemoveCommand.class,
                CustomResponseCheckCommand.class
        }
)
public class CustomResponseCommand implements GroupCommand {
}
