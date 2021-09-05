package com.ericlam.qqbot.valbot.response.group;

import com.ericlam.qqbot.valbot.response.GroupChatResponse;
import com.ericlam.qqbot.valbot.service.ValDataService;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomResponse implements GroupChatResponse {

    @Autowired
    private ValDataService dataService;

    @Nullable
    @Override
    public String onResponse(GroupMessageEvent event) {
        return dataService.getData().responses.get(event.getMessage());
    }
}
