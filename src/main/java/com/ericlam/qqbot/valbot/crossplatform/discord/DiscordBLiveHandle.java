package com.ericlam.qqbot.valbot.crossplatform.discord;

import com.ericlam.qqbot.valbot.crossplatform.BLiveHandle;
import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.entity.channel.NewsChannel;

import java.io.IOException;

public interface DiscordBLiveHandle extends BLiveHandle {


    void handle(GuildMessageChannel channel, long room, BLiveWebSocketData ws) throws IOException;


}
