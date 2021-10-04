package com.ericlam.qqbot.valbot.command.twitter;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.GroupCommand;

@ChatCommand(
        name = "twitter",
        description = "推特指令",
        alias = {"推特"},
        subCommands = {
                TwitterListenCommand.class,
                TwitterTerminateCommand.class,
                TwitterListeningCommand.class
        }
)
public class TwitterCommand implements GroupCommand {
}
