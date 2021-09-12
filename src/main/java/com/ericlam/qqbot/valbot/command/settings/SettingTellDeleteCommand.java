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
        name = "telldelete",
        description = "显示撤回的消息"
)
public class SettingTellDeleteCommand implements QQGroupCommand, DiscordGroupCommand {

    private final ValBotData.CommonSettings settings;

    public SettingTellDeleteCommand(ValDataService dataService){
        this.settings = dataService.getData().settings;
    }

    @Override
    public void executeCommand(GuildMessageChannel channel, MessageCreateEvent event, List<String> args) {
        settings.verboseDelete = !settings.verboseDelete;
        channel.createMessage(spec ->
                spec.setMessageReference(event.getMessage().getId())
                        .setContent("已成功设置显示撤回消息为: "+ settings.verboseDelete)).subscribe();
    }

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        settings.verboseDelete = !settings.verboseDelete;
        bot.sendGroupMsg(event.getGroupId(), "已成功设置显示撤回消息为: "+ settings.verboseDelete, true);
    }
}
