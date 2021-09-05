package com.ericlam.qqbot.valbot.response.group;

import com.ericlam.qqbot.valbot.response.GroupChatResponse;
import com.ericlam.qqbot.valbot.service.YesNoDataService;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class YesNoResponse implements GroupChatResponse {

    @Autowired
    private YesNoDataService yesNoService;

    @Nullable
    @Override
    public String onResponse(GroupMessageEvent event) {
        return yesNoService.getYesNoAnswer(event.getMessage());
    }

}
