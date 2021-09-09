package com.ericlam.qqbot.valbot.command.response;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.GroupCommand;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQGroupCommand;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;

import java.util.List;

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
