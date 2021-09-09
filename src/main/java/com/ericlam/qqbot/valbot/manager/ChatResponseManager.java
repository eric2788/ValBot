package com.ericlam.qqbot.valbot.manager;

import com.ericlam.qqbot.valbot.crossplatform.ChatResponse;
import com.ericlam.qqbot.valbot.crossplatform.MessageEventSource;
import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordChatResponse;
import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordMessageEventSource;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQChatResponse;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQMessageEventSource;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import org.slf4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class ChatResponseManager {

    @Autowired
    private BeanFactory factory;

    @Resource(name = "chat-responses")
    private List<Class<? extends ChatResponse>> groupChatResponses;

    @Autowired
    private Logger logger;


    public boolean onReceiveMessage(MessageEventSource source) {
        for (Class<? extends ChatResponse> resCls : groupChatResponses) {
            ChatResponse res = factory.getBean(resCls);

            // if more than two platforms, use data structure
            // 若平台超过两个，则使用 data structure
            if (source instanceof QQMessageEventSource qqMessageEventSource) {
                GroupMessageEvent event = qqMessageEventSource.messageEvent();
                Bot qqBot = qqMessageEventSource.qqBot();
                if (res instanceof QQChatResponse qqChatResponse) {
                    String response = qqChatResponse.onQQResponse(event);
                    if (response != null) {
                        String responseMessage = MsgUtils.builder().text(response).build();
                        qqBot.sendGroupMsg(event.getGroupId(), responseMessage, true);
                        return false;
                    }
                }

            } else if (source instanceof DiscordMessageEventSource discordMessageEventSource) {
                MessageChannel channel = discordMessageEventSource.channel();
                MessageCreateEvent event = discordMessageEventSource.event();
                if (res instanceof DiscordChatResponse discordChatResponse) {
                    String response = discordChatResponse.onDiscordResponse(event);
                    if (response != null) {
                        channel.createMessage(response).subscribe();
                        return false;
                    }
                }
            }
        }
        return true;
    }

}
