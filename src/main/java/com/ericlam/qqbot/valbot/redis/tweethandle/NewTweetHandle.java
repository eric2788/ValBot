package com.ericlam.qqbot.valbot.redis.tweethandle;

import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordTweetHandle;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQTweetHandle;
import com.ericlam.qqbot.valbot.dto.TweetStreamData;
import com.mikuac.shiro.common.utils.MsgUtils;
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
public class NewTweetHandle implements QQTweetHandle, DiscordTweetHandle {

    @Resource(name = "random")
    private Color randomColor;

    @Autowired
    private DateFormat dateFormat;

    @Override
    public void handle(GuildMessageChannel channel, String username, TweetStreamData data) throws IOException {
        channel.createMessage(spec -> {
            spec.addEmbed(em -> {
                em.setColor(randomColor);
                em.setAuthor(data.user.name, "https://twitter.com/" + username, data.user.profile_image_url_https);
                em.setDescription(MessageFormat.format("{0}({1}) 发布了一则新推文: ", data.user.name, username));
                em.addField("内容", data.text, false);
                if (data.entities.urls != null && !data.entities.urls.isEmpty()) {
                    em.addField("链接", data.entities.urls.stream().map(m -> m.expanded_url).collect(Collectors.joining("\n")), false);
                }
                em.setTimestamp(Instant.ofEpochMilli(data.timestamp_ms));
            });
            if (data.extended_entities != null && data.extended_entities.media != null ){
                for (TweetStreamData.Media media : data.extended_entities.media) {
                    spec.addEmbed(em -> em.setImage(media.media_url_https));
                }
            }
        }).subscribe();
    }

    @Override
    public void handle(Bot bot, long groupId, String username, TweetStreamData data) throws IOException {
        var builder = MsgUtils.builder()
                .text(data.user.name + " 发布了一则新推文: ").text("\n")
                .text(data.text).text("\n");
        bot.sendGroupMsg(groupId, ReplyTweetHandle.createTweetMessage(data, builder), false);
    }
}
