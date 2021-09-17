package com.ericlam.qqbot.valbot.crossplatform.discord;

import com.ericlam.qqbot.valbot.crossplatform.livehandle.YTLiveHandle;
import com.ericlam.qqbot.valbot.dto.YoutubeLiveInfo;
import discord4j.core.object.entity.channel.GuildMessageChannel;

import java.io.IOException;

public interface DiscordYTLiveHandle extends YTLiveHandle {

    void handle(GuildMessageChannel channel, String channelId, YoutubeLiveInfo info) throws IOException;

}
