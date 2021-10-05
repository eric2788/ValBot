package com.ericlam.qqbot.valbot.redis.tweethandle;

import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordTweetHandle;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQTweetHandle;
import com.ericlam.qqbot.valbot.dto.TweetStreamData;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.rest.util.Color;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.stream.Collectors;

@Component
public class ReplyTweetHandle implements DiscordTweetHandle, QQTweetHandle {

    @Resource(name = "random")
    private Color randomColor;

    @Override
    public void handle(GuildMessageChannel channel, String username, TweetStreamData data) throws IOException {
        channel.createMessage(spec -> {
            spec.addEmbed(em -> {
                em.setColor(randomColor);
                em.setAuthor(data.user.name, "https://twitter.com/" + username, data.user.profile_image_url_https);
                em.setThumbnail(data.user.profile_background_image_url_https);
                em.setDescription(MessageFormat.format("{0}({1}) 回复了 {2} 的一则推文: ", data.user.name, username, data.in_reply_to_screen_name));
                em.addField("回复贴文", MessageFormat.format("https://twitter.com/{0}/status/{1}", data.in_reply_to_screen_name, data.in_reply_to_status_id_str), false);
                em.addField("回复内容", data.text, false);
                if (data.entities.urls != null && !data.entities.urls.isEmpty()) {
                    em.addField("链接", data.entities.urls.stream().map(m -> m.url).collect(Collectors.joining("\n")), false);
                }
                em.setTimestamp(Instant.ofEpochMilli(data.timestamp_ms));
            });
            if (data.extended_entities != null && data.extended_entities.media != null){
                for (TweetStreamData.Media media : data.extended_entities.media) {
                    spec.addEmbed(em -> em.setImage(media.media_url_https));
                }
            }
        }).subscribe();
    }

    @Override
    public void handle(Bot bot, long groupId, String username, TweetStreamData data) throws IOException {
        var builder = MsgUtils.builder()
                .text(MessageFormat.format("{0}({1}) 回复了 {2} 的一则推文: ", data.user.name, username, data.in_reply_to_screen_name)).text("\n")
                .text("原帖: "+MessageFormat.format("https://twitter.com/{0}/status/{1}", data.in_reply_to_screen_name, data.in_reply_to_status_id_str)).text("\n")
                .text("内容: "+data.text).text("\n");
        bot.sendGroupMsg(groupId, createTweetMessage(data, builder), false);
    }

    static String createTweetMessage(TweetStreamData data, MsgUtils builder) {
        if (data.entities.urls != null && !data.entities.urls.isEmpty()) {
            builder.text("链接: ").text("\n");
            for (var url : data.entities.urls) {
                builder.text(url.expanded_url).text("\n");
            }
        }
        if (data.extended_entities != null && data.extended_entities.media != null) {
            for (var media : data.extended_entities.media) {
                builder.img(media.media_url_https);
            }
        }
        return builder.build();
    }
}
