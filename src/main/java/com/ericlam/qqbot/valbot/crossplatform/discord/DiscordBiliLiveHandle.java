package com.ericlam.qqbot.valbot.crossplatform.discord;

import com.ericlam.qqbot.valbot.crossplatform.livehandle.BiliLiveHandle;
import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
import discord4j.core.object.entity.channel.GuildMessageChannel;

import java.io.IOException;

public interface DiscordBiliLiveHandle extends BiliLiveHandle {


    void handle(GuildMessageChannel channel, long room, BLiveWebSocketData ws) throws IOException;


}
