package com.ericlam.qqbot.valbot.redis.wshandle;

import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class BroadcastHandle implements BLiveHandle {

    @Value("${val.group}")
    private long groupId;

    @Override
    public void handle(Bot bot, long room, BLiveWebSocketData ws) throws IOException {
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

}
