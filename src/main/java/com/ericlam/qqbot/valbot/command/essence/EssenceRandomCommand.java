package com.ericlam.qqbot.valbot.command.essence;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordGroupCommand;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQGroupCommand;
import com.ericlam.qqbot.valbot.service.QQBotService;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
@ChatCommand(
        name = "random",
        alias = {"随机"},
        description = "获取随机一条群精华消息"
)
public class EssenceRandomCommand implements QQGroupCommand, DiscordGroupCommand {

    private static final Random RANDOM = new Random();

    @Autowired
    private QQBotService botService;

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        var list = botService.getGroupEssenceMsgList(bot, event.getGroupId()).getData();
        if (list.isEmpty()){
            bot.sendGroupMsg(event.getGroupId(), MsgUtils.builder().reply(event.getMessageId()).text("无群精华消息。").build(), false);
            return;
        }
        var random = list.get(RANDOM.nextInt(list.size()));
        var message = botService.getMessage(bot, random.getMessageId()).getData();
        bot.sendGroupMsg(event.getGroupId(), message.getRawMessage(), false);
    }

    @Override
    public void executeCommand(GuildMessageChannel channel, MessageCreateEvent event, List<String> args) {

        channel.getPinnedMessages().hasElements().filter(b -> !b).flatMap(b -> channel.createMessage(spec -> {
            spec.setContent("无群精华消息。");
            spec.setMessageReference(event.getMessage().getId());
        })).subscribe();

        channel.getPinnedMessages()
                .reduce((a, b) -> RANDOM.nextBoolean() ? a : b)
                .flatMap(msg -> channel.createMessage(msg.getContent()))
                .subscribe();
    }
}
