package com.ericlam.qqbot.valbot.service;

import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
import com.ericlam.qqbot.valbot.dto.ValBotData;
import com.ericlam.qqbot.valbot.redis.BilibiliLiveSubscriber;
import org.slf4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Service
public class BilibiliLiveService {

    @Autowired
    private RedisMessageListenerContainer redisListener;

    @Autowired
    private BeanFactory factory;

    @Autowired
    private Logger logger;


    @Autowired
    private ValDataService dataService;

    private final Map<Long, MessageListener> listenerMap = new HashMap<>();

    private final ValBotData.BLiveSettings bLiveSettings;

    public BilibiliLiveService(ValDataService dataService) {
        this.bLiveSettings = dataService.getData().bLiveSettings;
    }

    public Set<Long> getLiveRoomListening() {
        return listenerMap.keySet();
    }

    public boolean isNotHighLightUser(long userId) {
        return !bLiveSettings.highlightUsers.contains(userId);
    }

    public boolean isVerBose() {
        return bLiveSettings.verbose;
    }


    @PostConstruct
    public void onCreate() {
        for (Long toListen : Set.copyOf(dataService.getData().bLiveSettings.listening)) {
            this.startListenInternal(toListen, false);
        }
    }

    public boolean startListen(long roomId) {
        return this.startListenInternal(roomId, true);
    }

    private boolean startListenInternal(long roomId, boolean external) {
        if (listenerMap.containsKey(roomId)) return false;
        var topic = new ChannelTopic("blive:" + roomId);
        var listener = factory.getBean(BilibiliLiveSubscriber.class);
        redisListener.addMessageListener(listener, topic);
        this.listenerMap.put(roomId, listener);
        if (external) bLiveSettings.listening.add(roomId);
        return true;
    }


    public boolean stopListen(long roomId) {
        var topic = new ChannelTopic("blive:" + roomId);
        var listener = listenerMap.remove(roomId);
        if (listener == null) {
            logger.info("找不到此 roomId 所屬監聽器。");
            return false;
        }
        redisListener.removeMessageListener(listener, topic);
        bLiveSettings.listening.remove(roomId);
        return true;
    }

}
