package com.ericlam.qqbot.valbot.filter;

import com.ericlam.qqbot.valbot.RequestException;
import com.ericlam.qqbot.valbot.configuration.properties.DiscordConfig;
import com.ericlam.qqbot.valbot.dto.res.ForwardedContent;
import com.ericlam.qqbot.valbot.service.QQBotService;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.scheduler.forkjoin.ForkJoinPoolScheduler;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ForkJoinPool;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class ForwardToDiscordFilter extends BotPlugin {

    private static final Pattern FORWARD_PATTERN = Pattern.compile(".*\\[CQ:forward,id=(.+)\\].*");

    @Autowired
    private GatewayDiscordClient client;

    @Autowired
    private DiscordConfig discordConfig;

    @Autowired
    private QQBotService botService;

    @Autowired
    private Logger logger;

    @Override
    public int onPrivateMessage(@NotNull Bot bot, @NotNull PrivateMessageEvent event) {
        Matcher matcher = FORWARD_PATTERN.matcher(event.getMessage());
        var find = matcher.find();
        logger.debug("收到群聊消息: {} (是否转发: {})", event.getMessage(), find);
        if (!find) return MESSAGE_IGNORE;
        bot.sendPrivateMsg(event.getUserId(), "正在转发消息内容到Discord...", false);
        List<ForwardedContent.ForwardMessage> list;
        try {
            list = botService.getForwardMsg(bot, matcher.group(1)).getData().getMessages();
        }catch (Exception e){
            bot.sendPrivateMsg(event.getUserId(), "消息转发失败:"+e.getMessage(), true);
            return MESSAGE_BLOCK;
        }
        Flux.just(list.toArray(ForwardedContent.ForwardMessage[]::new))
                .subscribeOn(ForkJoinPoolScheduler.create("forward-to-discord")) // async
                .flatMap(msg -> Flux.just(ShiroUtils.getMsgImgUrlList(msg.getContent()).toArray(String[]::new)))
                .concatMap(s -> client.getChannelById(Snowflake.of(discordConfig.getNsfwChannel()))
                        .ofType(GuildMessageChannel.class).flatMap(ch -> ch.createMessage(s)))
                .doOnComplete(() -> bot.sendPrivateMsg(event.getUserId(), "消息转发成功", true))
                .doOnError(ex -> bot.sendPrivateMsg(event.getUserId(), "消息转发失败:"+ex.getMessage(), true))
                .subscribe();
        return MESSAGE_BLOCK;
    }
}
