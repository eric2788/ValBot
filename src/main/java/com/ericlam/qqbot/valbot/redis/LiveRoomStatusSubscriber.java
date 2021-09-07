package com.ericlam.qqbot.valbot.redis;

import com.ericlam.qqbot.valbot.dto.LiveRoomStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;

@Component
public class LiveRoomStatusSubscriber extends BotMessageListener {

    private static final Map<String, String> translateStatus = Map.of(
            "started", "的监听已开始",
            "stopped", "的监听已关闭",
            "existed", "的监听已经启动了，因此请求被忽略",
            "server-closed", "已关闭",
            "server-started", "已启动"
    );

    @Autowired
    private ObjectMapper mapper;

    @Value("${val.group}")
    private long groupId;

    @Autowired
    private Logger logger;


    @Override
    public void onRedisMessage(Bot bot, @NotNull Message message, byte[] bytes) {
        String channel = new String(message.getChannel());
        if (!channel.equals("live-room-status")) {
            logger.warn("房间状态订阅 接收了 非 live-room-status 频道的讯息: {}", channel);
            return;
        }
        try {
            LiveRoomStatus status = mapper.readValue(message.getBody(), LiveRoomStatus.class);
            String room = status.id == -1 ? "监控服务器" : "房间 " + status.id;
            String statusTxt = translateStatus.getOrDefault(status.status, status.status);
            String msg = MsgUtils.builder().text(room).text(" ").text(statusTxt).text("。").build();
            bot.sendGroupMsg(groupId, msg, true);
        } catch (IOException e) {
            bot.sendGroupMsg(groupId, "解析WS數據時出現錯誤: " + e.getMessage(), true);
            logger.warn("Error while parsing data ", e);
        }
    }
}
