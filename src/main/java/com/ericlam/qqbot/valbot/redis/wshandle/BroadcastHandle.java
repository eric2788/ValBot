package com.ericlam.qqbot.valbot.redis.wshandle;

import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordBLiveHandle;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQBLiveHandle;
import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.discordjson.json.EmojiData;
import discord4j.rest.util.Color;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.MessageFormat;

@Component
public class BroadcastHandle implements QQBLiveHandle, DiscordBLiveHandle {


    @Resource(name = "random")
    private Color randomColor;

    @Override
    public void handle(Bot bot, long groupId, long room, BLiveWebSocketData ws) throws IOException {
        var builder = MsgUtils.builder()
                .text(ws.data.name).text(" 正在直播").text("\n")
                .text("标题: ").text(ws.data.title).text("\n")
                .text("直播间: ").text("https://live.bilibili.com/").text(String.valueOf(room));
        if (ws.data.cover != null) {
            builder.img(ws.data.cover);
        }
        bot.sendGroupMsg(groupId, builder.build(), false);
    }

    @Override
    public void handle(GuildMessageChannel channel, long room, BLiveWebSocketData ws) throws IOException {
        channel.createMessage(spec -> {
            spec.addEmbed(em -> {
                em.setDescription(MessageFormat.format("[{0}]({1}) 正在直播", ws.data.name, "https://space.bilibili.com/"+ws.data.uid));
                em.setColor(randomColor);
                em.addField("标题", ws.data.title, false);
                em.addField("房间号", String.valueOf(room), false);
                if (ws.data.cover != null){
                    em.setImage(ws.data.cover);
                }
            });
            spec.setComponents(
                    ActionRow.of(Button.link("https://live.bilibili.com/" + room, ReactionEmoji.unicode("\uD83D\uDEAA"), "进入直播间"))
            );
        }).subscribe();
    }
}
