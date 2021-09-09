package com.ericlam.qqbot.valbot.command.test;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.GroupCommand;

@ChatCommand(
        name = "test",
        description = "测试指令",
        subCommands = {
                TestBroadcastCommand.class,
                TestDanmuCommand.class,
                TestRoomEnterCommand.class,
                TestSuperChatCommand.class
        }
)
public class TestCommand implements GroupCommand {
}
