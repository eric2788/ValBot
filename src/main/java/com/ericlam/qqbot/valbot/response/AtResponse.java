package com.ericlam.qqbot.valbot.response;

import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordChatResponse;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQChatResponse;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import discord4j.common.util.Snowflake;
import discord4j.core.event.domain.message.MessageCreateEvent;
import org.jetbrains.annotations.Nullable;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

@Component
public class AtResponse implements QQChatResponse, DiscordChatResponse {

    @Value("${val.bot}")
    private long qqBotId;

    @Resource(name = "discord-bot-id")
    private Snowflake discordBotId;

    @Autowired
    private BotContainer container;

    @Nullable
    @Override
    public String onDiscordResponse(MessageCreateEvent event) {
        if (event.getMessage().getUserMentionIds().contains(discordBotId)){
            return "啥？你说啥？我听不懂";
        }
        return null;
    }

    @Nullable
    @Override
    public String onQQResponse(GroupMessageEvent event) {
        if (ShiroUtils.getAtList(event.getArrayMsg()).contains(qqBotId)){
            return "啥？你说啥？我听不懂";
        }
        return null;
    }
}
