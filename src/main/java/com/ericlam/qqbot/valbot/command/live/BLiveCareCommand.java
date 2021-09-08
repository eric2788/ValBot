package com.ericlam.qqbot.valbot.command.live;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.command.GroupChatCommand;
import com.ericlam.qqbot.valbot.service.ValDataService;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ChatCommand(
        name = "care",
        alias = {"关注", "高亮"},
        description = "新增高亮用户",
        placeholders = {"<用户ID>"}
)
public class BLiveCareCommand implements GroupChatCommand {


    @Autowired
    private ValDataService dataService;

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        long userId;
        try {
            userId = Long.parseLong(args.get(0));
        }catch (NumberFormatException e){
            bot.sendGroupMsg(event.getGroupId(), MsgUtils
                    .builder()
                    .text("无效的用户ID")
                    .reply(event.getMessageId())
                    .build(), false);
            return;
        }
        if (dataService.getData().bLiveSettings.highlightUsers.contains(userId)){
            bot.sendGroupMsg(event.getGroupId(),
                    MsgUtils
                            .builder()
                            .text(userId+" 已经存在了")
                            .reply(event.getMessageId())
                            .build(), false);
            return;
        }
        dataService.getData().bLiveSettings.highlightUsers.add(userId);
        bot.sendGroupMsg(event.getGroupId(), MsgUtils
                .builder()
                .text("新增高亮用户 "+userId+" 成功。")
                .reply(event.getMessageId())
                .build(), false);
    }
}
