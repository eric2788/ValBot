package com.ericlam.qqbot.valbot.filter;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ValGroupFilter extends BotPlugin {

    private final Logger Logger;


    @Value("${val.group}")
    private long groupId;

    public ValGroupFilter(Logger Logger) {
        this.Logger = Logger;
    }

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
}
