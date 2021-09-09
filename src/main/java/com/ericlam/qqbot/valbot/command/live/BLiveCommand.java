package com.ericlam.qqbot.valbot.command.live;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.GroupCommand;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQGroupCommand;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;

import java.util.List;


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
                BLiveVerboseCommand.class
        }
)
public class BLiveCommand implements GroupCommand {
}
