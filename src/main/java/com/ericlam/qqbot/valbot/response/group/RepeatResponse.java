package com.ericlam.qqbot.valbot.response.group;

import com.ericlam.qqbot.valbot.response.GroupChatResponse;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Component;

@Component
public class RepeatResponse implements GroupChatResponse {

    @Nullable
    @Override
    public String onResponse(GroupMessageEvent event) {
        if (!event.getMessage().startsWith("复读")) return null;
        return event.getMessage().replace("复读", "");
    }

}
