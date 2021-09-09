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
public class RoomEnterHandle implements QQBLiveHandle, DiscordBLiveHandle {


    @Autowired
    private BilibiliLiveService liveService;

    @Autowired
    private Logger logger;

    @Resource(name = "random")
    private Color randomColor;

    @Override
    public void handle(Bot bot, long groupId, long room, BLiveWebSocketData ws) throws IOException {
        var data = ws.data.content.getJSONObject("data");
        var uname = data.getString("uname");
        var uid = data.getLong("uid");
        if (liveService.isNotHighLightUser(uid)) return;
        logger.info("高亮用戶 {} 進入了 {} 的直播間", uname, ws.data.name);
        String msg = MsgUtils.builder().text("噔噔咚！").text("你所关注的用户 ").text(uname).text("进入了 ").text(ws.data.name).text(" 的直播间。").build();
        bot.sendGroupMsg(groupId, msg, true);
    }

    @Override
    public void handle(NewsChannel channel, long room, BLiveWebSocketData ws) throws IOException {
        var data = ws.data.content.getJSONObject("data");
        var uname = data.getString("uname");
        var uid = data.getLong("uid");
        if (liveService.isNotHighLightUser(uid)) return;
        logger.info("高亮用戶 {} 進入了 {} 的直播間", uname, ws.data.name);
        channel.createMessage(spec -> {
            spec.addEmbed(em -> {
                em.setTitle("噔噔咚！");
                em.setColor(randomColor);
                em.setDescription("你所关注的用户 " + uname + " 进入了 " + ws.data.name + " 的直播间。");
                em.addField("用户", uname, true);
                em.addField("进入直播间", ws.data.name, true);
            });
        }).subscribe();
    }
}
