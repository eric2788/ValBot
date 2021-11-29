package com.ericlam.qqbot.valbot.redis.blivehandle;

import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordBiliLiveHandle;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQBiliLiveHandle;
import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.rest.util.Color;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.MessageFormat;

@Component
public class BroadcastHandle implements QQBiliLiveHandle, DiscordBiliLiveHandle {


    @Resource(name = "random")
    private Color randomColor;

    @Override
    public void handle(Bot bot, long groupId, long room, BLiveWebSocketData ws) throws IOException {
        var builder = MsgUtils.builder()
                .text(ws.live_info.name).text(" 正在B站直播").text("\n")
                .text("标题: ").text(ws.live_info.title).text("\n")
                .text("直播间: ").text("https://live.bilibili.com/").text(String.valueOf(room));
        if (ws.live_info.cover != null) {
            builder.img(ws.live_info.cover);
        }
        bot.sendGroupMsg(groupId, builder.build(), false);
    }

    @Override
    public void handle(GuildMessageChannel channel, long room, BLiveWebSocketData ws) throws IOException {
        channel.createMessage(spec -> {
            spec.addEmbed(em -> {
                em.setDescription(MessageFormat.format("[{0}]({1}) 正在B站直播", ws.live_info.name, "https://space.bilibili.com/"+ws.live_info.uid));
                em.setColor(randomColor);
                em.addField("标题", ws.live_info.title, false);
                em.addField("房间号", String.valueOf(room), false);
                if (ws.live_info.cover != null){
                    em.setImage(ws.live_info.cover);
                }
            });
            spec.setComponents(
                    ActionRow.of(Button.link("https://live.bilibili.com/" + room, ReactionEmoji.unicode("\uD83D\uDEAA"), "进入直播间"))
            );
        }).subscribe();
    }
}
