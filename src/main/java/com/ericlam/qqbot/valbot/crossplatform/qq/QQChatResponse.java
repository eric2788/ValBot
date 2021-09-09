package com.ericlam.qqbot.valbot.crossplatform.qq;

import com.ericlam.qqbot.valbot.crossplatform.ChatResponse;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;

import javax.annotation.Nullable;

public interface QQChatResponse extends ChatResponse {

    @Nullable
    String onQQResponse(GroupMessageEvent event);


}
