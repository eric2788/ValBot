package com.ericlam.qqbot.valbot.redis.blivehandle;

import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordBiliLiveHandle;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQBiliLiveHandle;
import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
import com.ericlam.qqbot.valbot.dto.SuperChatMessage;
import com.ericlam.qqbot.valbot.service.BilibiliLiveService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
public class SuperChatHandle implements QQBiliLiveHandle, DiscordBiliLiveHandle {

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
        if (liveService.isNotHighLightUser(sc.uid) && !ws.command.equals(BLiveWebSocketData.CommandType.BOT_TESTING)) return;
        logger.info("在 {} 的直播間 收到高亮用戶 {} 價值 ￥{} 的 SC 訊息: {}", ws.data.name, sc.user_info.uname, sc.price, sc.message);
        String msg = MsgUtils.builder().text("在 ").text(ws.data.name).text(" 的直播间收到来自 ").text(sc.user_info.uname).text(" 的醒目留言").text("\n")
                .text("￥ ").text(String.valueOf(sc.price)).text("\n")
                .text("「").text(sc.message).text("」").build();
        bot.sendGroupMsg(groupId, msg, true);
    }

    @Override
    public void handle(GuildMessageChannel channel, long room, BLiveWebSocketData ws) throws IOException {
        var sc = mapper.readValue(ws.data.content.getJSONObject("data").toJSONString(), SuperChatMessage.class);
        if (liveService.isNotHighLightUser(sc.uid) && !ws.command.equals(BLiveWebSocketData.CommandType.BOT_TESTING)) return;
        logger.info("在 {} 的直播間 收到高亮用戶 {} 價值 ￥{} 的 SC 訊息: {}", ws.data.name, sc.user_info.uname, sc.price, sc.message);
        channel.createMessage(spec -> {
            spec.addEmbed(em -> {
                em.setDescription(MessageFormat.format("在 {0} 的直播间收到来自 [{1}]({2}) 的醒目留言。", ws.data.name, sc.user_info.uname, "https://space.bilibili.com/"+sc.uid));
                em.setAuthor(sc.user_info.uname, "https://space.bilibili.com/"+sc.uid, sc.user_info.face);
                em.setColor(randomColor);
                em.addField("￥", String.valueOf(sc.price), true);
                em.addField("房间号", String.valueOf(ws.data.room), true);
                em.addField("内容", "「"+sc.message+"」", false);
            });
            spec.setComponents(
                    ActionRow.of(Button.link("https://live.bilibili.com/"+ws.data.room, ReactionEmoji.unicode("\uD83D\uDEAA"), "点击围观"))
            );
        }).subscribe();
    }
}
