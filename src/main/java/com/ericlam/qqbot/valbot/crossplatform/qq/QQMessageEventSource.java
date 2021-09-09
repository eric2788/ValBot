package com.ericlam.qqbot.valbot.crossplatform.qq;

import com.ericlam.qqbot.valbot.crossplatform.MessageEventSource;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;

public record QQMessageEventSource(GroupMessageEvent messageEvent, Bot qqBot) implements MessageEventSource {
}
