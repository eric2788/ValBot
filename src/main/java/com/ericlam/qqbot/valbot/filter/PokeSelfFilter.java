package com.ericlam.qqbot.valbot.filter;

import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.notice.PokeNoticeEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;

@Component
public class PokeSelfFilter extends BotPlugin {

    @Override
    public int onGroupPokeNotice(@NotNull Bot bot, @NotNull PokeNoticeEvent event) {
        if (event.getTargetId() != bot.getSelfId()) return MESSAGE_IGNORE;
        bot.sendGroupMsg(event.getGroupId(), MsgUtils.builder()
                .at(event.getUserId())
                .poke(event.getUserId())
                .text("戳你妹")
                .build(), false);
        return MESSAGE_BLOCK;
    }
}
