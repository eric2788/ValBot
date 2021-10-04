package com.ericlam.qqbot.valbot.redis.tweethandle;

import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordTweetHandle;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQTweetHandle;
import com.ericlam.qqbot.valbot.dto.TweetStreamData;
import com.mikuac.shiro.core.Bot;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.rest.util.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.stream.Collectors;

@Component
public class RetweetHandle implements DiscordTweetHandle, QQTweetHandle {

    @Resource(name = "random")
    private Color randomColor;

    @Override
    public void handle(GuildMessageChannel channel, String username, TweetStreamData data) throws IOException {
        channel.createMessage(spec -> {
            spec.addEmbed(em -> {
                em.setColor(randomColor);
                em.setAuthor(data.user.name, "https://twitter.com/"+username, data.user.profile_image_url_https);
                em.setThumbnail(data.user.profile_background_image_url_https);
                em.setDescription(MessageFormat.format("{0}({1}) 转发了一则推文: ", data.user.name, username));
                em.addField("", data.text, false);
                if (data.entities.urls != null && !data.entities.urls.isEmpty()) {
                    em.addField("链接", data.entities.urls.stream().map(m -> m.url).collect(Collectors.joining("\n")), false);
                }
                em.setTimestamp(Instant.ofEpochMilli(data.timestamp_ms));
            });
            if (data.entities.media != null ){
                for (TweetStreamData.Media media : data.entities.media) {
                    spec.addEmbed(em -> em.setImage(media.media_url_https));
                }
            }
        }).subscribe();
    }

    @Override
    public void handle(Bot bot, long groupId, String username, TweetStreamData data) throws IOException {
        // skip (防止洗屏)
    }
}
