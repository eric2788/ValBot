package com.ericlam.qqbot.valbot.redis.ythandle;

import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordYTLiveHandle;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQYTLiveHandle;
import com.ericlam.qqbot.valbot.dto.YoutubeLiveInfo;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.text.MessageFormat;

@Component
public class LiveEndHandle implements DiscordYTLiveHandle, QQYTLiveHandle {

    @Override
    public void handle(GuildMessageChannel channel, String channelId, YoutubeLiveInfo info) throws IOException {
        channel.createMessage(spec -> {
            spec.addEmbed(em -> {
                em.setDescription(MessageFormat.format("[{0}]({1}) 的油管直播已结束。", info.channelName, "https://youtube.com/channel/"+info.channelId));
            });
        }).subscribe();
    }

    @Override
    public void handle(Bot bot, long groupId, String channelId, YoutubeLiveInfo info) throws IOException {
        String msg = MsgUtils.builder().text(info.channelName).text(" 的油管直播已结束。").build();
        bot.sendGroupMsg(groupId, msg, false);
    }
}
