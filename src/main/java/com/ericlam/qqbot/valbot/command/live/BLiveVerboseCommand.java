package com.ericlam.qqbot.valbot.command.live;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordGroupCommand;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQGroupCommand;
import com.ericlam.qqbot.valbot.dto.ValBotData;
import com.ericlam.qqbot.valbot.service.ValDataService;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.MessageChannel;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@ChatCommand(
        name = "verbose",
        description = "切换是否广播监听状态",
        alias = {"切换广播"}
)
public class BLiveVerboseCommand implements QQGroupCommand, DiscordGroupCommand {

    private final ValBotData.BLiveSettings settings;

    public BLiveVerboseCommand(ValDataService dataService){
        this.settings = dataService.getData().bLiveSettings;
    }

    @Override
    public void executeCommand(MessageChannel channel, MessageCreateEvent event, List<String> args) {
        settings.verbose = !settings.verbose;
        String msg = "成功切换广播状态为 "+settings.verbose;
        channel.createMessage(spec -> spec.setContent(msg).setMessageReference(event.getMessage().getId())).subscribe();
    }

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        settings.verbose = !settings.verbose;
        String msg = "成功切换广播状态为 "+settings.verbose;
        bot.sendGroupMsg(event.getGroupId(), msg, true);
    }
}
