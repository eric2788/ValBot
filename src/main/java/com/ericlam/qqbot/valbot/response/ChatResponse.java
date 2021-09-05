package com.ericlam.qqbot.valbot.response;

import com.mikuac.shiro.dto.event.message.MessageEvent;

import javax.annotation.Nullable;

public interface ChatResponse<T extends MessageEvent> {

    @Nullable
    String onResponse(T event);

}
