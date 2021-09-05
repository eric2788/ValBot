package com.ericlam.qqbot.valbot.command;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ChatCommand(
        name = "say",
        description = "说话指令",
        alias = {"speak", "说话", "说", "复读"},
        placeholders = "<讯息>"
)
public class SpeakCommand implements GroupChatCommand{

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        String speak = String.join(" ", args);
        bot.sendGroupMsg(event.getGroupId(), speak, true);
    }
}
