package com.ericlam.qqbot.valbot.command.random;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.GroupCommand;

@ChatCommand(
        name = "random",
        alias = {"随机"},
        description = "随机指令",
        subCommands = {
                RandomEssenceCommand.class,
                RandomMemberCommand.class
        }
)
public class RandomCommand implements GroupCommand {
}
