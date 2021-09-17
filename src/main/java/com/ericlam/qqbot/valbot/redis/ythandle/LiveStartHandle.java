package com.ericlam.qqbot.valbot.redis.ythandle;

import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordYTLiveHandle;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQYTLiveHandle;
import com.ericlam.qqbot.valbot.dto.YoutubeLiveInfo;
import com.mikuac.shiro.core.Bot;
import discord4j.core.object.entity.channel.GuildMessageChannel;

import java.io.IOException;

public class LiveStartHandle implements DiscordYTLiveHandle, QQYTLiveHandle {


    @Override
    public void handle(GuildMessageChannel channel, String channelId, YoutubeLiveInfo info) throws IOException {
        // TODO: send message
    }

    @Override
    public void handle(Bot bot, long groupId, String channelId, YoutubeLiveInfo info) throws IOException {
        // TODO: send message
    }
}
