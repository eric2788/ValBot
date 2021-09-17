package com.ericlam.qqbot.valbot.command.settings;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.GroupCommand;

@ChatCommand(
        name = "settings",
        description = "设定指令",
        alias = {"设定"},
        subCommands = {
                SettingTellDeleteCommand.class,
                SettingYearlyCheckCommand.class,
                SettingVerboseCommand.class
        }
)
public class SettingCommand implements GroupCommand {
}
