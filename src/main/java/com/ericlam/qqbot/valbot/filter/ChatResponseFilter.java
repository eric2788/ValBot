package com.ericlam.qqbot.valbot.filter;

import com.ericlam.qqbot.valbot.crossplatform.qq.QQMessageEventSource;
import com.ericlam.qqbot.valbot.manager.ChatResponseManager;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChatResponseFilter extends BotPlugin {


    @Autowired
    private ChatResponseManager responseManager;

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        var result = responseManager.onReceiveMessage(new QQMessageEventSource(event, bot));
        return result ? MESSAGE_BLOCK : MESSAGE_IGNORE;
    }


}
