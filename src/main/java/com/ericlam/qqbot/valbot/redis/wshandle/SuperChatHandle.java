package com.ericlam.qqbot.valbot.redis.wshandle;

import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordBLiveHandle;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQBLiveHandle;
import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
import com.ericlam.qqbot.valbot.dto.SuperChatMessage;
import com.ericlam.qqbot.valbot.service.BilibiliLiveService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import discord4j.core.object.entity.channel.NewsChannel;
import discord4j.rest.util.Color;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;

@Component
public class SuperChatHandle implements QQBLiveHandle, DiscordBLiveHandle {

    @Autowired
    private ObjectMapper mapper;


    @Autowired
    private BilibiliLiveService liveService;

    @Autowired
    private Logger logger;

    @Resource(name = "random")
    private Color randomColor;

    @Override
    public void handle(Bot bot, long groupId, long room, BLiveWebSocketData ws) throws IOException {
        var sc = mapper.readValue(ws.data.content.getJSONObject("data").toJSONString(), SuperChatMessage.class);
        if (liveService.isNotHighLightUser(sc.uid)) return;
        logger.info("在 {} 的直播間 收到高亮用戶 {} 價值 ￥{} 的 SC 訊息: {}", ws.data.name, sc.user_info.uname, sc.price, sc.message);
        String msg = MsgUtils.builder().text("从房间 ").text(ws.data.name).text(" 收到高亮用戶的 SC: ").text("\n")
                .text("￥ ").text(String.valueOf(sc.price)).text("\n")
                .text("「").text(sc.message).text("」").text("\n")
                .text("用戶: ").text(sc.user_info.uname).build();
        bot.sendGroupMsg(groupId, msg, true);
    }

    @Override
    public void handle(NewsChannel channel, long room, BLiveWebSocketData ws) throws IOException {
        var sc = mapper.readValue(ws.data.content.getJSONObject("data").toJSONString(), SuperChatMessage.class);
        if (liveService.isNotHighLightUser(sc.uid)) return;
        logger.info("在 {} 的直播間 收到高亮用戶 {} 價值 ￥{} 的 SC 訊息: {}", ws.data.name, sc.user_info.uname, sc.price, sc.message);
        channel.createMessage(spec -> {
            spec.addEmbed(em -> {
                em.setTitle("收到高亮用户的 SuperChat");
                em.setColor(randomColor);
                em.addField("价值", "￥"+sc.price, true);
                em.addField("用户", sc.user_info.uname, true);
                em.addField("直播间", ws.data.name, true);
                em.addField("内容", "「"+sc.message+"」", false);
            });
        }).subscribe();
    }
}
