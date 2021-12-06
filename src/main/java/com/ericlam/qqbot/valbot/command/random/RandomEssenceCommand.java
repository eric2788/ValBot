package com.ericlam.qqbot.valbot.command.random;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordGroupCommand;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQGroupCommand;
import com.ericlam.qqbot.valbot.dto.res.EssenceInfo;
import com.ericlam.qqbot.valbot.service.QQBotService;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Component
@ChatCommand(
        name = "essence",
        alias = {"群精华"},
        description = "获取随机一条群精华消息"
)
public class RandomEssenceCommand implements QQGroupCommand, DiscordGroupCommand {

    @Autowired
    private Random random;

    @Autowired
    private QQBotService botService;

    @Autowired
    private Logger logger;

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        Flux.defer(() -> Flux.fromIterable(botService.getGroupEssenceMsgList(bot, event.getGroupId()).getData()))
                .doOnError(ex -> {
                    ex.printStackTrace();
                    bot.sendGroupMsg(event.getGroupId(), MsgUtils.builder()
                            .text("獲取群消息时出现错误: " + ex.getMessage())
                            .reply(event.getMessageId())
                            .build(), false);
                })
                .mapNotNull(msg -> {
                    var data = bot.getMsg(msg.getMessageId());
                    if (data.getRetcode() == -1) {
                        return null;
                    } else {
                        return data.getData();
                    }
                })
                .collect(Collectors.toList())
                .subscribe(list -> {
                    if (list.isEmpty()) {
                        bot.sendGroupMsg(event.getGroupId(), MsgUtils.builder().reply(event.getMessageId()).text("无群精华消息。").build(), false);
                        return;
                    }
                    var message = list.get(this.random.nextInt(list.size()));
                    var msg = Optional.ofNullable(message.getRawMessage()).orElseGet(message::getMessage);
                    logger.debug("正在发送随机群精华消息: {}", msg);
                    bot.sendGroupMsg(event.getGroupId(), msg, false);
                });


    }

    @Override
    public void executeCommand(GuildMessageChannel channel, MessageCreateEvent event, List<String> args) {
        channel.getPinnedMessages().hasElements().filter(b -> !b).flatMap(b -> channel.createMessage(spec -> {
            spec.setContent("无订选消息。");
            spec.setMessageReference(event.getMessage().getId());
        })).subscribe();

        channel.getPinnedMessages()
                .reduce((a, b) -> this.random.nextBoolean() ? a : b)
                .flatMap(msg -> channel.createMessage(msg.getContent()))
                .subscribe();
    }
}
