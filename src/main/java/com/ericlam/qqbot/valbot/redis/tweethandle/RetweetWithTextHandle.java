package com.ericlam.qqbot.valbot.redis.tweethandle;

import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordTweetHandle;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQTweetHandle;
import com.ericlam.qqbot.valbot.dto.TweetStreamData;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.spec.EmbedCreateSpec;
import discord4j.rest.util.Color;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.MessageFormat;
import java.time.Instant;
import java.util.stream.Collectors;

@Component
public class RetweetWithTextHandle implements DiscordTweetHandle, QQTweetHandle {

    @Resource(name = "random")
    private Color randomColor;

    @Override
    public void handle(GuildMessageChannel channel, String username, TweetStreamData data) throws IOException {
        channel.createMessage(spec -> {
            spec.addEmbed(em -> {
                em.setColor(randomColor);
                em.setAuthor(data.user.name, "https://twitter.com/"+username, data.user.profile_image_url_https);
                em.setDescription(MessageFormat.format("{0}({1}) 转发了一则推文: ", data.user.name, username));
                if (data.text != null){
                    em.addField("附文", data.text, false);
                }
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
            if (data.quoted_status != null){
                var status = data.quoted_status;
                RetweetHandle.createRetweetEmbed(spec, status, randomColor);
                if (status.entities.media != null){
                    for (TweetStreamData.Media media : status.entities.media) {
                        spec.addEmbed(em -> em.setImage(media.media_url_https));
                    }
                }
            }
        }).subscribe();
    }

    @Override
    public void handle(Bot bot, long groupId, String username, TweetStreamData data) throws IOException {
        var builder = MsgUtils.builder()
                .text(data.user.name + " 分享了一则推文: ").text("\n");
                if (data.quoted_status != null){
                    builder.text("转发贴文: "+MessageFormat.format("https://twitter.com/{0}/status/{1}", data.quoted_status.user.screen_name, data.quoted_status_id_str)).text("\n");
                }
                builder.text("附文: "+data.text).text("\n");
        bot.sendGroupMsg(groupId, ReplyTweetHandle.createTweetMessage(data, builder), false);
    }

}
