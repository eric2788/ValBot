package com.ericlam.qqbot.valbot.redis.wshandle;

import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordBLiveHandle;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQBLiveHandle;
import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
import com.ericlam.qqbot.valbot.service.BilibiliLiveService;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import discord4j.core.object.component.ActionRow;
import discord4j.core.object.component.Button;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.reaction.ReactionEmoji;
import discord4j.rest.util.Color;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.text.MessageFormat;

@Component
public class DanmuHandle implements QQBLiveHandle, DiscordBLiveHandle {


    @Autowired
    private BilibiliLiveService liveService;

    @Autowired
    private Logger logger;

    @Resource(name = "random")
    private Color randomColor;

    @Override
    public void handle(Bot bot, long groupId, long room, BLiveWebSocketData ws) throws IOException {
        var data = ws.data.content.getJSONArray("info");
        var danmaku = data.getString(1);
        var uname = data.getJSONArray(2).getString(1);
        var uid = data.getJSONArray(2).getLong(0);
        if (liveService.isNotHighLightUser(uid) && !ws.command.equals(BLiveWebSocketData.CommandType.BOT_TESTING)) return;
        logger.info("檢測到高亮用戶 {} 在 {} 的直播間發送了彈幕訊息: {}", uname, ws.data.name, danmaku);
        String msg = MsgUtils.builder()
                .text(uname).text(" 在 ").text(ws.data.name).text(" 的直播间发送了一条消息").text("\n")
                .text("弹幕: ").text(danmaku)
                .build();
        bot.sendGroupMsg(groupId, msg, true);
    }

    @Override
    public void handle(GuildMessageChannel channel, long room, BLiveWebSocketData ws) throws IOException {
        var data = ws.data.content.getJSONArray("info");
        var danmaku = data.getString(1);
        var uname = data.getJSONArray(2).getString(1);
        var uid = data.getJSONArray(2).getLong(0);
        if (liveService.isNotHighLightUser(uid) && !ws.command.equals(BLiveWebSocketData.CommandType.BOT_TESTING)) return;
        logger.info("檢測到高亮用戶 {} 在 {} 的直播間發送了彈幕訊息: {}", uname, ws.data.name, danmaku);
        channel.createMessage(spec -> {
            spec.addEmbed( em ->{
                em.setDescription(MessageFormat.format("[{0}]({1}) 在 {2} 的直播间发送了一条消息", uname, "https://space.bilibili.com/"+uid, ws.data.name));
                em.setColor(randomColor);
                em.addField("弹幕", danmaku, false);
            });
            spec.setComponents(
                    ActionRow.of(Button.link("https://live.bilibili.com/"+ws.data.room, ReactionEmoji.unicode("\uD83D\uDEAA"), "点击围观"))
            );
        }).subscribe();
    }
}
