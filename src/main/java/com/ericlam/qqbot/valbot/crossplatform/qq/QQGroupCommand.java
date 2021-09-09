package com.ericlam.qqbot.valbot.crossplatform.qq;

import com.ericlam.qqbot.valbot.crossplatform.GroupCommand;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;

import java.util.List;

public interface QQGroupCommand extends GroupCommand {

    void executeCommand(Bot bot, GroupMessageEvent event, List<String> args);


}
