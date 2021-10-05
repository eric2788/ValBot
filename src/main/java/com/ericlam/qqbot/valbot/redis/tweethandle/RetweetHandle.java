package com.ericlam.qqbot.valbot.redis.tweethandle;

import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordTweetHandle;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQTweetHandle;
import com.ericlam.qqbot.valbot.dto.TweetStreamData;
import com.mikuac.shiro.core.Bot;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.spec.MessageCreateSpec;
import discord4j.rest.util.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.Optional;
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
                em.setDescription(MessageFormat.format("{0}({1}) 转发了一则推文: ", data.user.name, username));
                em.setTimestamp(Instant.ofEpochMilli(data.timestamp_ms));
            });
            if (data.retweeted_status != null){
                var status = data.retweeted_status;
                createRetweetEmbed(spec, status, randomColor);
            }
        }).subscribe();
    }

    static void createRetweetEmbed( MessageCreateSpec spec, TweetStreamData status, Color randomColor) {
        spec.addEmbed(em -> {
            em.setColor(randomColor);
            em.setAuthor(status.user.name, "https://twitter.com/"+status.user.screen_name, status.user.profile_image_url_https);
            if (status.text != null){
                em.addField("内容", status.text, false);
            }
            if (status.entities.urls != null && !status.entities.urls.isEmpty()) {
                em.addField("链接", status.entities.urls.stream().map(m -> m.expanded_url).collect(Collectors.joining("\n")), false);
            }
        });
        if (status.extended_entities != null && status.extended_entities.media != null){
            for (TweetStreamData.Media media : status.extended_entities.media) {
                spec.addEmbed(em -> em.setImage(media.media_url_https));
            }
        }
    }

    @Override
    public void handle(Bot bot, long groupId, String username, TweetStreamData data) throws IOException {
        // skip (防止洗屏)
    }
}
