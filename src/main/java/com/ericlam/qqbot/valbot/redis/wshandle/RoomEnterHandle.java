package com.ericlam.qqbot.valbot.redis.wshandle;

import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
import com.ericlam.qqbot.valbot.service.BilibiliLiveService;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RoomEnterHandle implements BLiveHandle{

    @Value("${val.group}")
    private long groupId;

    @Autowired
    private BilibiliLiveService liveService;

    @Autowired
    private Logger logger;

    @Override
    public void handle(Bot bot, long room, BLiveWebSocketData ws) throws IOException {
        var data = ws.data.content.getJSONObject("data");
        var uname = data.getString("uname");
        var uid = data.getLong("uid");
        if (liveService.isNotHighLightUser(uid)) return;
        logger.info("高亮用戶 {} 進入了 {} 的直播間", uname, ws.data.name);
        String msg = MsgUtils.builder().text("噔噔咚！").text("你所关注的用户 ").text(uname).text("进入了 ").text(ws.data.name).text(" 的直播间。").build();
        bot.sendGroupMsg(groupId, msg, true);
    }
}
