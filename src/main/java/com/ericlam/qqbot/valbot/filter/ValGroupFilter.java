package com.ericlam.qqbot.valbot.filter;

import com.ericlam.qqbot.valbot.dto.ValBotData;
import com.ericlam.qqbot.valbot.service.ValDataService;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.dto.event.notice.GroupMsgDeleteNoticeEvent;
import com.mikuac.shiro.dto.event.request.FriendAddRequestEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ValGroupFilter extends BotPlugin {

    @Autowired
    private Logger Logger;

    private final ValBotData.CommonSettings settings;

    public ValGroupFilter(ValDataService dataService) {
        settings = dataService.getData().settings;
    }

    @Value("${val.group}")
    private long groupId;

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        if (event.getGroupId() != groupId) {
            Logger.info("非瓦群，已无视");
            return MESSAGE_BLOCK;
        }
        return MESSAGE_IGNORE;
    }

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull PrivateMessageEvent event) {
        Logger.info("收到私讯，正在发送勿扰讯息。");
        String msg = MsgUtils.builder().text("WDNMD，别私我谢谢").build();
        bot.sendPrivateMsg(event.getUserId(), msg, false);
        return MESSAGE_BLOCK;
    }

    @Override
    public int onFriendAddRequest(@NotNull Bot bot, @NotNull FriendAddRequestEvent event) {
        var list = bot.getGroupMemberList(groupId);
        if (list.getRetcode() != 0) return MESSAGE_IGNORE; // request error
        if (list.getData().stream().noneMatch(member -> member.getUserId() == event.getUserId()))
            return MESSAGE_IGNORE; //必須為群友
        bot.setFriendAddRequest(event.getFlag(), true, "我的好友");
        return MESSAGE_BLOCK;
    }

    @Override
    public int onGroupMsgDeleteNotice(@NotNull Bot bot, @NotNull GroupMsgDeleteNoticeEvent event) {
        if (settings.verboseDelete) {
            var deletedMsg = bot.getMsg((int) event.getMsgId());
            if (deletedMsg.getRetcode() == -1) return MESSAGE_IGNORE;
            bot.sendGroupMsg(event.getGroupId(), MsgUtils.builder().at(event.getOperatorId()).text(" 所撤回的消息:").build(), false);
            var message = deletedMsg.getData();
            var msg = Optional.ofNullable(message.getRawMessage()).orElseGet(message::getMessage);
            bot.sendGroupMsg(event.getGroupId(), msg, false);
        }
        return MESSAGE_IGNORE;
    }
}
