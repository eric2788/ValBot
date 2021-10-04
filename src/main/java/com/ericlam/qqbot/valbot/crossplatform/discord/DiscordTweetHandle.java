package com.ericlam.qqbot.valbot.crossplatform.discord;

import com.ericlam.qqbot.valbot.crossplatform.livehandle.TweetsHandle;
import com.ericlam.qqbot.valbot.dto.TweetStreamData;
import discord4j.core.object.entity.channel.GuildMessageChannel;

import java.io.IOException;

public interface DiscordTweetHandle extends TweetsHandle {

    void handle(GuildMessageChannel channel, String username, TweetStreamData data) throws IOException;

}
