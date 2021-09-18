package com.ericlam.qqbot.valbot.command.youtube;


import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.GroupCommand;

@ChatCommand(
        name = "youtube",
        description = "油管指令",
        subCommands = {
                YoutubeListenCommand.class,
                YoutubeTerminateCommand.class
        }
)
public class YoutubeCommand implements GroupCommand {
}
