package com.ericlam.qqbot.valbot.command.yesno;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordGroupCommand;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQGroupCommand;
import com.ericlam.qqbot.valbot.service.ValDataService;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.rest.util.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

@Component
@ChatCommand(
        name = "check",
        description = "检查所有答案"
)
public class YesNoCheckCommand implements QQGroupCommand, DiscordGroupCommand {

    @Autowired
    private ValDataService dataService;

    @Resource(name = "random")
    private Color randomColor;

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        StringBuilder builder = new StringBuilder("正在打印所有内容").append("\n");
        dataService.getData().answers.forEach((q, r) -> {
            builder.append(q).append("=").append(r).append("\n");
        });
        bot.sendGroupMsg(event.getGroupId(), MsgUtils
                .builder()
                .text(builder.toString())
                .reply(event.getMessageId())
                .build(), false);
    }

    @Override
    public void executeCommand(GuildMessageChannel channel, MessageCreateEvent event, List<String> args) {
        StringBuilder builder = new StringBuilder();
        dataService.getData().answers.forEach((q, r) -> {
            builder.append(q).append("=").append(r).append("\n");
        });
        if (dataService.getData().answers.isEmpty()){
            builder.append("无内容");
        }
        channel.createMessage(spec -> {
            spec.setMessageReference(event.getMessage().getId());
            spec.addEmbed(em -> {
                em.setColor(randomColor);
                em.addField("正在打印所有内容", builder.toString(), false);
            });
        }).subscribe();
    }
}
