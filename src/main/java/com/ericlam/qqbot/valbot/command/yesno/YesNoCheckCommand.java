package com.ericlam.qqbot.valbot.command.yesno;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.command.GroupChatCommand;
import com.ericlam.qqbot.valbot.service.ValDataService;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ChatCommand(
        name = "check",
        description = "检查所有答案"
)
public class YesNoCheckCommand implements GroupChatCommand {

    @Autowired
    private ValDataService dataService;

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        StringBuilder builder = new StringBuilder("正在打印所有内容").append("\n");
        dataService.getData().answers.forEach((q, r) -> {
            builder.append(q).append("=").append(r).append("\n");
        });
        bot.sendGroupMsg(event.getGroupId(), builder.toString(), true);
    }
}
