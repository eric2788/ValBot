package com.ericlam.qqbot.valbot.response;

import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordChatResponse;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQChatResponse;
import com.ericlam.qqbot.valbot.service.YesNoDataService;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class YesNoResponse implements QQChatResponse, DiscordChatResponse {

    @Autowired
    private YesNoDataService yesNoService;

    @Nullable
    @Override
    public String onQQResponse(GroupMessageEvent event) {
        return yesNoService.getYesNoAnswer(event.getMessage());
    }

    @Nullable
    @Override
    public String onDiscordResponse(MessageCreateEvent event) {
        return yesNoService.getYesNoAnswer(event.getMessage().getContent());
    }
}
