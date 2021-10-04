package com.ericlam.qqbot.valbot.redis.tweethandle;

import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordTweetHandle;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQTweetHandle;
import com.ericlam.qqbot.valbot.dto.TweetStreamData;
import com.mikuac.shiro.core.Bot;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class DeleteTweetHandle implements DiscordTweetHandle, QQTweetHandle {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeleteTweetHandle.class);

    @Override
    public void handle(GuildMessageChannel channel, String username, TweetStreamData data) throws IOException {
        LOGGER.info("{} 刪除了一則推文。", username);
        // skip
    }

    @Override
    public void handle(Bot bot, long groupId, String username, TweetStreamData data) throws IOException {
        LOGGER.info("{} 刪除了一則推文。", username);
        // skip
    }
}
