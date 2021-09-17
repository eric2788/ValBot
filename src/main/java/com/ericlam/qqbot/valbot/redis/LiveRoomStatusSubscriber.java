package com.ericlam.qqbot.valbot.redis;

import com.ericlam.qqbot.valbot.crossplatform.subscriber.LiveSubscriber;
import com.ericlam.qqbot.valbot.dto.LiveRoomStatus;
import com.ericlam.qqbot.valbot.service.BilibiliLiveService;
import com.ericlam.qqbot.valbot.service.ValDataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.List;

@Component
public class LiveRoomStatusSubscriber implements MessageListener {


    @Autowired
    private ValDataService dataService;

    @Autowired
    private ObjectMapper mapper;


    @Resource(name = "ws-subscribers")
    private List<? extends LiveSubscriber> bLiveSubscribers;

    @Autowired
    private Logger logger;


    @Override
    public void onMessage(@Nonnull Message message, byte[] bytes) {
        String channel = new String(message.getChannel());
        if (!channel.equals("live-room-status")) {
            logger.warn("房间状态订阅 接收了 非 live-room-status 频道的讯息: {}", channel);
            return;
        }
        if (!dataService.getData().settings.verbose) {
            logger.info("Verbose 為 False, 故不輸出任何狀態訊息。");
            return;
        }
        try {
            LiveRoomStatus status = mapper.readValue(message.getBody(), LiveRoomStatus.class);
            bLiveSubscribers.forEach(sub -> sub.subscribeLiveStatus(status));
        } catch (IOException e) {
            if (dataService.getData().settings.verbose){
                bLiveSubscribers.forEach(sub -> sub.doOnError(e, "server"));
            }
            logger.warn("解析WS數據時出現錯誤: ", e);
        }
    }
}
