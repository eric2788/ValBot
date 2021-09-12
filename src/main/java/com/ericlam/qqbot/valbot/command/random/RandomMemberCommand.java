package com.ericlam.qqbot.valbot.command.random;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordGroupCommand;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQGroupCommand;
import com.ericlam.qqbot.valbot.service.QQBotService;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Guild;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.rest.util.AllowedMentions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Random;

@Component
@ChatCommand(
        name = "member",
        alias = {"成员"},
        description = "随机群成员指令"
)
public class RandomMemberCommand implements QQGroupCommand, DiscordGroupCommand {


    @Autowired
    private Random random;

    @Autowired
    private QQBotService botService;

    @Override
    public void executeCommand(GuildMessageChannel channel, MessageCreateEvent event, List<String> args) {
        channel.getGuild().flux().flatMap(Guild::getMembers)
                .reduce((a, b) -> random.nextBoolean() ? a : b)
                .flatMap(member -> channel.createMessage(spec ->
                        spec.setAllowedMentions(
                                AllowedMentions.builder().allowUser(member.getId()).build()
                        ).setContent(member.getNicknameMention()))).subscribe();
    }

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        var list = botService.validateNotError(bot.getGroupMemberList(event.getGroupId())).getData();
        var random = list.get(this.random.nextInt(list.size()));
        bot.sendGroupMsg(event.getGroupId(), MsgUtils.builder().at(random.getUserId()).build(), false);
    }
}
