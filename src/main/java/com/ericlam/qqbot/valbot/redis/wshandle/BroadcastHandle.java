package com.ericlam.qqbot.valbot.redis.wshandle;

import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordBLiveHandle;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQBLiveHandle;
import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import discord4j.core.object.entity.channel.NewsChannel;
import discord4j.rest.util.Color;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
public class BroadcastHandle implements QQBLiveHandle, DiscordBLiveHandle {


    @Resource(name = "random")
    private Color randomColor;

    @Override
    public void handle(Bot bot, long groupId, long room, BLiveWebSocketData ws) throws IOException {
        var builder = MsgUtils.builder()
                .text("收到开播通知:").text("\n")
                .text("标题: ").text(ws.data.title).text("\n")
                .text("用户: ").text(ws.data.name).text("(" + ws.data.uid + ")").text("\n")
                .text("连结: ").text("https://live.bilibili.com/").text(String.valueOf(room));
        if (ws.data.cover != null) {
            builder.img(ws.data.cover);
        }
        bot.sendGroupMsg(groupId, builder.build(), false);
    }

    @Override
    public void handle(NewsChannel channel, long room, BLiveWebSocketData ws) throws IOException {
        channel.createMessage(spec -> {
            spec.addEmbed(em -> {
                em.setTitle("收到开播通知");
                em.setColor(randomColor);
                em.setDescription("从B站直播间收到了开播通知");
                em.addField("标题", ws.data.title, true);
                em.addField("用户", ws.data.name + "("+ws.data.uid+")", true);
                em.addField("连结", "https://live.bilibili.com/" + room, true);
                if (ws.data.cover != null){
                    em.setImage(ws.data.cover);
                }
            });
        }).subscribe();
    }
}
