package com.ericlam.qqbot.valbot.redis.blivehandle;

import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordBiliLiveHandle;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQBiliLiveHandle;
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
public class RoomEnterHandle implements QQBiliLiveHandle, DiscordBiliLiveHandle {


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
        if (liveService.isNotHighLightUser(uid) && !ws.command.equals(BLiveWebSocketData.CommandType.BOT_TESTING)) return;
        logger.info("高亮用戶 {} 進入了 {} 的直播間", uname, ws.data.name);
        String msg = MsgUtils.builder().text("噔噔咚！").text("你所关注的用户 ").text(uname).text(" 进入了 ").text(ws.data.name).text(" 的直播间。").build();
        bot.sendGroupMsg(groupId, msg, true);
    }

    @Override
    public void handle(GuildMessageChannel channel, long room, BLiveWebSocketData ws) throws IOException {
        var data = ws.data.content.getJSONObject("data");
        var uname = data.getString("uname");
        var uid = data.getLong("uid");
        if (liveService.isNotHighLightUser(uid) && !ws.command.equals(BLiveWebSocketData.CommandType.BOT_TESTING)) return;
        logger.info("高亮用戶 {} 進入了 {} 的直播間", uname, ws.data.name);
        channel.createMessage(spec -> {
            spec.addEmbed(em -> {
                em.setColor(randomColor);
                em.setDescription(MessageFormat.format("噔噔咚！ 你所关注的用户 [{0}]({1}) 进入了 {2} 的直播间。", uname, "https://space.bilibili.com/"+uid, ws.data.name));
                em.addField("房间号", String.valueOf(ws.data.room), false);
            });
            spec.setComponents(
                    ActionRow.of(Button.link("https://live.bilibili.com/"+ws.data.room, ReactionEmoji.unicode("\uD83D\uDEAA"), "点击围观"))
            );
        }).subscribe();
    }
}
