package com.ericlam.qqbot.valbot.command.yesno;


import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordGroupCommand;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQGroupCommand;
import com.ericlam.qqbot.valbot.service.ArgParseService;
import com.ericlam.qqbot.valbot.service.YesNoDataService;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import discord4j.rest.util.AllowedMentions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

@Component
@ChatCommand(
        name = "set",
        description = "设置yes no答案",
        placeholders = {"<问题>" ,"<true | false>"}
)
public class YesNoSetCommand implements QQGroupCommand, DiscordGroupCommand {

    @Autowired
    private YesNoDataService yesNoDataService;

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        var question = args.get(0);
        if (yesNoDataService.isInvalidQuestion(question)){
            bot.sendGroupMsg(event.getGroupId(), "不是一个有效的问题", true);
            return;
        }
        var result = Boolean.parseBoolean(args.get(1));
        yesNoDataService.setYesNoAnswer(question, result);
        String msg = MessageFormat.format("已成功设置 {0} 答案为 {1}", question, result);
        bot.sendGroupMsg(event.getGroupId(), MsgUtils
                .builder()
                .text(msg)
                .reply(event.getMessageId())
                .build(), false);
    }

    @Override
    public void executeCommand(MessageChannel channel, MessageCreateEvent event, List<String> args) {
        var question = args.get(0);
        if (yesNoDataService.isInvalidQuestion(question)){
            channel.createMessage(spec -> spec.setMessageReference(event.getMessage().getId()).setContent("不是一个有效的问题")).subscribe();
            return;
        }
        var result = Boolean.parseBoolean(args.get(1));
        yesNoDataService.setYesNoAnswer(question, result);
        String msg = MessageFormat.format("已成功设置 {0} 答案为 {1}", question, result);
        channel.createMessage(spec -> spec.setContent(msg).setMessageReference(event.getMessage().getId())).subscribe();
    }
}
