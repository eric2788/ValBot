package com.ericlam.qqbot.valbot.redis.ythandle;

import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordYTLiveHandle;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQYTLiveHandle;
import com.ericlam.qqbot.valbot.dto.YoutubeLiveInfo;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.rest.util.Color;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.Optional;
import java.util.TimeZone;

@Component
public class LiveStartHandle implements DiscordYTLiveHandle, QQYTLiveHandle {

    @Resource(name = "random")
    private Color randomColor;

    @Autowired
    private DateFormat dateFormat;

    @Override
    public void handle(GuildMessageChannel channel, String channelId, YoutubeLiveInfo info) throws IOException {
        channel.createMessage(spec -> {
            spec.addEmbed(em -> {
                em.setDescription(MessageFormat.format("[{0}]({1}) 正在油管直播", info.channelName, "https://youtube.com/channel/"+info.channelId));
                em.setColor(randomColor);
                var broadcastInfo = info.info;
                if (broadcastInfo != null){
                    em.addField("标题", broadcastInfo.title, false);
                    em.addField("发布时间", dateFormat.format(broadcastInfo.publishTime), false);
                    if (broadcastInfo.cover != null){
                        em.setImage(broadcastInfo.cover);
                    }
                }else{
                    em.addField("获取直播资讯失败", "无法获取直播资讯，可直接点击进入直播间按钮查看。", false);
                }
            });
            spec.setComponents(
                    ActionRow.of(Button.link(getUrl(info), ReactionEmoji.unicode("\uD83D\uDEAA"), "进入直播间"))
            );
        }).subscribe();
    }

    @Override
    public void handle(Bot bot, long groupId, String channelId, YoutubeLiveInfo info) throws IOException {
        var builder = MsgUtils.builder().text(info.channelName).text(" 正在油管直播").text("\n");
        var broadcastInfo = info.info;
        if (broadcastInfo != null){
            builder//.text("标题: ").text(broadcastInfo.title).text("\n")
                    .text("开始时间: ").text(dateFormat.format(broadcastInfo.publishTime)).text("\n")
                    .text("直播间: ").text(getUrl(info));
            if (broadcastInfo.cover != null) {
                builder.img(broadcastInfo.cover);
            }
        }else{
            builder.text("直播间： ").text(MessageFormat.format("https://youtube.com/channel/{0}/live", info.channelId));
        }
        bot.sendGroupMsg(groupId, builder.build(), false);
    }



    static String getUrl(YoutubeLiveInfo info){
        return Optional.ofNullable(info.info).map(b -> "https://youtu.be/"+b.id).orElseGet(() -> MessageFormat.format("https://youtube.com/channel/{0}/live", info.channelId));
    }
}
