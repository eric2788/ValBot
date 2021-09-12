package com.ericlam.qqbot.valbot.command.settings;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordGroupCommand;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQGroupCommand;
import com.ericlam.qqbot.valbot.dto.ValBotData;
import com.ericlam.qqbot.valbot.service.ValDataService;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ChatCommand(
        name = "yearly",
        description = "设置群精华消息检查间隔"
)
public class SettingYearlyCheckCommand implements DiscordGroupCommand, QQGroupCommand {

    private final ValBotData.CommonSettings commonSettings;

    public SettingYearlyCheckCommand(ValDataService dataService){
        this.commonSettings = dataService.getData().settings;
    }

    @Override
    public void executeCommand(GuildMessageChannel channel, MessageCreateEvent event, List<String> args) {
        commonSettings.yearlyCheck = !commonSettings.yearlyCheck;
        channel.createMessage(spec ->
                spec.setContent("已设置群精华消息检查间隔为 "+(commonSettings.yearlyCheck ? "每年":"每月"))
                        .setMessageReference(event.getMessage().getId())).subscribe();
    }

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        commonSettings.yearlyCheck = !commonSettings.yearlyCheck;
        bot.sendGroupMsg(event.getGroupId(), "已设置群精华消息检查间隔为 "+(commonSettings.yearlyCheck ? "每年":"每月"), true);
    }
}
