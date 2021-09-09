package com.ericlam.qqbot.valbot.command.response;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordGroupCommand;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQGroupCommand;
import com.ericlam.qqbot.valbot.service.ValDataService;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.MessageFormat;
import java.util.List;

@Component
@ChatCommand(
        name = "set",
        description = "设置自定义回应",
        placeholders = {"<文字>", "<回应>"}
)
public class CustomResponseSetCommand implements QQGroupCommand, DiscordGroupCommand {

    @Autowired
    private ValDataService dataService;

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        String txt = args.get(0);
        String res = args.get(1);
        dataService.getData().responses.put(txt, res);
        bot.sendGroupMsg(event.getGroupId(), MsgUtils
                .builder()
                .text(MessageFormat.format("已成功设置 {0} 的回应为 {1}", txt, res))
                .reply(event.getMessageId()).build(), false);
    }


    @Override
    public void executeCommand(MessageChannel channel, MessageCreateEvent event, List<String> args) {
        String txt = args.get(0);
        String res = args.get(1);
        dataService.getData().responses.put(txt, res);
        channel.createMessage(spec -> spec
                                .setContent(MessageFormat.format("已成功设置 {0} 的回应为 {1}", txt, res))
                                .setMessageReference(event.getMessage().getId()))
                .subscribe();
    }
}
