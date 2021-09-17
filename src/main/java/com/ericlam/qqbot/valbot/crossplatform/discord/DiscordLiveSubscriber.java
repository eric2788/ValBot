package com.ericlam.qqbot.valbot.crossplatform.discord;

import com.ericlam.qqbot.valbot.configuration.properties.DiscordConfig;
import com.ericlam.qqbot.valbot.crossplatform.livehandle.BiliLiveHandle;
import com.ericlam.qqbot.valbot.crossplatform.livehandle.YTLiveHandle;
import com.ericlam.qqbot.valbot.crossplatform.subscriber.BiliLiveSubscriber;
import com.ericlam.qqbot.valbot.crossplatform.subscriber.LiveSubscriber;
import com.ericlam.qqbot.valbot.crossplatform.subscriber.YTLiveSubscriber;
import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
import com.ericlam.qqbot.valbot.dto.LiveRoomStatus;
import com.ericlam.qqbot.valbot.dto.YoutubeLiveInfo;
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
public class DiscordLiveSubscriber implements LiveSubscriber, BiliLiveSubscriber, YTLiveSubscriber {

    private final long logChannel;
    private final long newsChannel;

    public DiscordLiveSubscriber(DiscordConfig discord){
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
    public void doOnError(IOException e, String room) {
        var logChannel = getLogChannel();
        if (logChannel.isEmpty()) {
            logger.warn("找不到广播频道 {} ，已略过。", this.logChannel);
            return;
        }
        var channel = logChannel.get();
        channel.createMessage(spec -> {
            spec.addEmbed(em -> {
                em.addField("解析WS时出现错误", e.getMessage(), false);
                em.addField("来源", room.equals("-1") || room.equals("server") ? "服务器" : "房间: " + room, false);
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
        String room = status.id.equals("-1") || status.id.equals("server") ? "监控服务器" : "房间 " + status.id;
        String prefix = "【"+ status.platform +"】 ";
        if (status.status.startsWith("error:")){
            String errorMsg = status.status.split(":")[1];
            String msg = prefix + room + " 初始化监听时出现错误: " + errorMsg;
            channel.createMessage(msg).subscribe();
            return;
        }
        String statusTxt = translation.getOrDefault(status.status, status.status);
        String msg = prefix + room + " " + statusTxt + "。";
        channel.createMessage(msg).subscribe();
    }

    private Optional<NewsChannel> getNewsChannel() {
        return client.getChannelById(Snowflake.of(newsChannel)).ofType(NewsChannel.class).blockOptional();
    }

    private Optional<TextChannel> getLogChannel() {
        return client.getChannelById(Snowflake.of(logChannel)).ofType(TextChannel.class).blockOptional();
    }

    @Override
    public void subscribe(BiliLiveHandle handle, long room, BLiveWebSocketData ws) throws IOException {
        if (!(handle instanceof DiscordBiliLiveHandle discordBLiveHandle)) return;
        var newsChannel = getNewsChannel();
        if (newsChannel.isEmpty()) {
            logger.warn("找不到广播频道 {} ，已略过。", logChannel);
            return;
        }
        var channel = newsChannel.get();
        discordBLiveHandle.handle(channel, room, ws);
    }


    @Override
    public void subscribe(YTLiveHandle handle, String channelId, YoutubeLiveInfo info) throws IOException {
        if (!(handle instanceof DiscordYTLiveHandle discordYTLiveHandle)) return;
        var newsChannel = getNewsChannel();
        if (newsChannel.isEmpty()) {
            logger.warn("找不到广播频道 {} ，已略过。", logChannel);
            return;
        }
        var channel = newsChannel.get();
        discordYTLiveHandle.handle(channel, channelId, info);
    }
}
