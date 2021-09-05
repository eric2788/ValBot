package com.ericlam.qqbot.valbot.filter;

import com.ericlam.qqbot.valbot.response.GroupChatResponse;
import com.ericlam.qqbot.valbot.response.PrivateChatResponse;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
public class ChatResponseFilter extends BotPlugin {

    @Autowired
    private BeanFactory factory;

    @Resource(name = "group-chat-responses")
    private List<Class<? extends GroupChatResponse>> groupChatResponses;

    @Resource(name = "private-chat-responses")
    private List<Class<? extends PrivateChatResponse>> privateChatResponses;


    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        for (Class<? extends GroupChatResponse> resCls : groupChatResponses) {
            GroupChatResponse res = factory.getBean(resCls);
            String response = res.onResponse(event);
            if (response != null) {
                String responseMessage = MsgUtils.builder().text(response).build();
                bot.sendGroupMsg(event.getGroupId(), responseMessage, true);
                return MESSAGE_BLOCK;
            }
        }
        return MESSAGE_IGNORE;
    }


    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull PrivateMessageEvent event) {
        for (Class<? extends PrivateChatResponse> resCls : privateChatResponses) {
            PrivateChatResponse res = factory.getBean(resCls);
            String response = res.onResponse(event);
            if (response != null) {
                bot.sendGroupMsg(event.getUserId(), response, true);
                return MESSAGE_BLOCK;
            }
        }
        return MESSAGE_IGNORE;
    }
}
