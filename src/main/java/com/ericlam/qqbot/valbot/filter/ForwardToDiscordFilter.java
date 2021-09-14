package com.ericlam.qqbot.valbot.filter;

import com.ericlam.qqbot.valbot.configuration.properties.DiscordConfig;
import com.ericlam.qqbot.valbot.dto.res.ForwardedContent;
import com.ericlam.qqbot.valbot.service.QQBotService;
import com.mikuac.shiro.common.utils.RegexUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import com.mikuac.shiro.enums.ShiroUtilsEnum;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;

import java.util.ArrayList;
import java.util.List;
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
        logger.debug("收到私聊消息: {} (是否转发: {})", event.getMessage(), find);
        if (!find) return MESSAGE_IGNORE;
        bot.sendPrivateMsg(event.getUserId(), "正在转发消息内容到Discord...", false);
        List<ForwardedContent.ForwardMessage> list;
        try {
            list = botService.getForwardMsg(bot, matcher.group(1)).getData().getMessages();
        } catch (Exception e) {
            e.printStackTrace();
            bot.sendPrivateMsg(event.getUserId(), "消息转发失败:" + e.getMessage(), true);
            return MESSAGE_BLOCK;
        }
        this.recursiveForward(bot, Flux.fromIterable(list))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(msg -> Flux.just(getMsgImgUrlList(msg.getContent()).toArray(String[]::new)))
                .distinct()
                .concatMap(s -> client.getChannelById(Snowflake.of(discordConfig.getNsfwChannel()))
                        .ofType(GuildMessageChannel.class).flatMap(ch -> ch.createMessage(s)))
                .doOnComplete(() -> bot.sendPrivateMsg(event.getUserId(), "消息转发成功", true))
                .doOnError(ex -> bot.sendPrivateMsg(event.getUserId(), "消息转发失败:" + ex.getMessage(), true))
                .subscribe();
        return MESSAGE_BLOCK;
    }


    private Flux<ForwardedContent.ForwardMessage> recursiveForward(Bot bot, Flux<ForwardedContent.ForwardMessage> messages) {
        return messages
                .publishOn(Schedulers.boundedElastic())
                .expandDeep(msg -> {
                    Matcher matcher = FORWARD_PATTERN.matcher(msg.getContent());
                    try {
                        if (matcher.find()) {
                            List<ForwardedContent.ForwardMessage> list = botService.getForwardMsg(bot, matcher.group(1)).getData().getMessages();
                            return this.recursiveForward(bot, Flux.fromIterable(list));
                        }
                    }catch (Exception e){
                        logger.warn("Error while parsing nested content: "+e.getMessage(), e);
                    }
                    return Flux.empty();
                });

    }

    public static List<String> getMsgImgUrlList(String msg) {
        List<String> imgUrlList = new ArrayList<>();
        for (String i : msg.split(ShiroUtilsEnum.CQ_CODE_SPLIT.getValue())) {
            if (i.startsWith("image")) {
                imgUrlList.add(RegexUtils.regex(ShiroUtilsEnum.GET_URL_REGEX.getValue(), i));
            }
        }
        return imgUrlList;
    }
}
