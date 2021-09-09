package com.ericlam.qqbot.valbot.crossplatform.discord;

import com.ericlam.qqbot.valbot.configuration.properties.DiscordConfig;
import com.ericlam.qqbot.valbot.crossplatform.BLiveHandle;
import com.ericlam.qqbot.valbot.crossplatform.BLiveSubscriber;
import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
import com.ericlam.qqbot.valbot.dto.LiveRoomStatus;
import discord4j.common.util.Snowflake;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.object.entity.channel.NewsChannel;
import discord4j.core.object.entity.channel.TextChannel;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
public class DiscordBLiveSubscriber implements BLiveSubscriber {

    private final long logChannel;
    private final long newsChannel;

    public DiscordBLiveSubscriber(DiscordConfig discord){
        this.logChannel = discord.getLogChannel();
        this.newsChannel = discord.getNewsChannel();
    }

    @Autowired
    private GatewayDiscordClient client;


    @Autowired
    private Logger logger;

    @Resource(name = "translate-status")
    private Map<String, String> translation;

    @Override
    public void subscribe(BLiveHandle handle, long room, BLiveWebSocketData ws) throws IOException {
        if (!(handle instanceof DiscordBLiveHandle discordBLiveHandle)) return;
        var newsChannel = getNewsChannel();
        if (newsChannel.isEmpty()) {
            logger.warn("找不到广播频道 {} ，已略过。", logChannel);
            return;
        }
        var channel = newsChannel.get();
        discordBLiveHandle.handle(channel, room, ws);
    }

    @Override
    public void doOnError(IOException e, long room) {
        var logChannel = getLogChannel();
        if (logChannel.isEmpty()) {
            logger.warn("找不到广播频道 {} ，已略过。", this.logChannel);
            return;
        }
        var channel = logChannel.get();
        channel.createMessage(spec -> {
            spec.addEmbed(em -> {
                em.addField("解析WS时出现错误", e.getMessage(), false);
                em.addField("来源", room == -1 ? "服务器" : "房间: " + room, false);
            });
        }).subscribe();
    }

    @Override
    public void subscribeLiveStatus(LiveRoomStatus status) {
        var logChannel = getLogChannel();
        if (logChannel.isEmpty()) {
            logger.warn("找不到广播频道 {} ，已略过。", this.logChannel);
            return;
        }
        var channel = logChannel.get();
        String room = status.id == -1 ? "监控服务器" : "房间 " + status.id;
        String statusTxt = translation.getOrDefault(status.status, status.status);
        String msg = room + " " + statusTxt + "。";
        channel.createMessage(msg).subscribe();
    }

    private Optional<NewsChannel> getNewsChannel() {
        return client.getChannelById(Snowflake.of(newsChannel)).ofType(NewsChannel.class).blockOptional();
    }

    private Optional<TextChannel> getLogChannel() {
        return client.getChannelById(Snowflake.of(logChannel)).ofType(TextChannel.class).blockOptional();
    }

}
