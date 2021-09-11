package com.ericlam.qqbot.valbot.command.essence;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.GroupCommand;

@ChatCommand(
        name = "essence",
        alias = {"精华"},
        description = "群精华消息指令",
        subCommands = {
                EssenceRandomCommand.class
        }
)
public class EssenceCommand implements GroupCommand {
}
