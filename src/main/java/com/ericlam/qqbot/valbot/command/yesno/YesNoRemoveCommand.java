package com.ericlam.qqbot.valbot.command.yesno;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.command.GroupChatCommand;
import com.ericlam.qqbot.valbot.service.YesNoDataService;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

@Component
@ChatCommand(
        name = "remove",
        description = "移除问题",
        placeholders = "<问题>"
)
public class YesNoRemoveCommand implements GroupChatCommand {


    @Autowired
    private YesNoDataService yesNoDataService;


    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        String question = args.get(0);
        String msg = yesNoDataService.removeYesNoAnswer(question) ? MessageFormat.format("已成功移除 {0} 的答案", question) : "找不到此问题";
        bot.sendGroupMsg(event.getGroupId(), msg, true);
    }
}
