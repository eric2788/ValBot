package com.ericlam.qqbot.valbot.redis.wshandle;

import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
import com.ericlam.qqbot.valbot.dto.SuperChatMessage;
import com.ericlam.qqbot.valbot.service.BilibiliLiveService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class SuperChatHandle implements BLiveHandle{

    @Autowired
    private ObjectMapper mapper;

    @Value("${val.group}")
    private long groupId;

    @Autowired
    private BilibiliLiveService liveService;

    @Autowired
    private Logger logger;

    @Override
    public void handle(Bot bot, long room, BLiveWebSocketData ws) throws IOException {
        var sc = mapper.readValue(ws.data.content.getJSONObject("data").toJSONString(), SuperChatMessage.class);
        if (liveService.isNotHighLightUser(sc.uid)) return;
        logger.info("在 {} 的直播間 收到高亮用戶 {} 價值 ￥{} 的 SC 訊息: {}", ws.data.name, sc.user_info.uname, sc.price, sc.message);
        String msg = MsgUtils.builder().text("从房间 ").text(ws.data.name).text(" 收到高亮用戶的 SC: ").text("\n")
                .text("￥ ").text(String.valueOf(sc.price)).text("\n")
                .text("「").text(sc.message).text("」").text("\n")
                .text("用戶: ").text(sc.user_info.uname).build();
        bot.sendGroupMsg(groupId, msg, true);
    }
}
