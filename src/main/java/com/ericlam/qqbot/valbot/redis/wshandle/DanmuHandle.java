package com.ericlam.qqbot.valbot.redis.wshandle;

import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordBLiveHandle;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQBLiveHandle;
import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
import com.ericlam.qqbot.valbot.service.BilibiliLiveService;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import discord4j.core.object.entity.channel.NewsChannel;
import discord4j.rest.util.Color;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

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
        if (liveService.isNotHighLightUser(uid)) return;
        logger.info("檢測到高亮用戶 {} 在 {} 的直播間發送了彈幕訊息: {}", uname, ws.data.name, danmaku);
        String msg = MsgUtils.builder()
                .text("从 ").text(ws.data.name).text(" 的直播房间收到高亮用戶的弹幕讯息:").text("\n")
                .text("用戶: ").text(uname).text("\n")
                .text("彈幕: ").text(danmaku).build();
        bot.sendGroupMsg(groupId, msg, true);
    }

    @Override
    public void handle(NewsChannel channel, long room, BLiveWebSocketData ws) throws IOException {
        var data = ws.data.content.getJSONArray("info");
        var danmaku = data.getString(1);
        var uname = data.getJSONArray(2).getString(1);
        var uid = data.getJSONArray(2).getLong(0);
        if (liveService.isNotHighLightUser(uid)) return;
        logger.info("檢測到高亮用戶 {} 在 {} 的直播間發送了彈幕訊息: {}", uname, ws.data.name, danmaku);
        channel.createMessage(spec -> {
            spec.addEmbed( em ->{
                em.setTitle("收到高亮用户的弹幕讯息");
                em.setColor(randomColor);
                em.addField("用户", uname, true);
                em.addField("直播间", ws.data.name, true);
                em.addField("弹幕", danmaku, false);
            });
        }).subscribe();
    }
}
