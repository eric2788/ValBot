package com.ericlam.qqbot.valbot.service;

import com.ericlam.qqbot.valbot.dto.ValBotData;
import com.ericlam.qqbot.valbot.redis.BilibiliLiveListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class BilibiliLiveService {

    @Autowired
    private RedisMessageListenerContainer redisListener;

    @Autowired
    private BeanFactory factory;

    @Autowired
    private Logger logger;


    @Autowired
    private ObjectMapper mapper;

    private final Map<Long, LiveInfo> roomInfo = new ConcurrentHashMap<>();

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


    @PostConstruct
    public void onCreate() {
        for (Long toListen : Set.copyOf(bLiveSettings.listening)) {
            this.startListenInternal(toListen, false);
        }
    }

    public boolean startListen(long roomId) {
        return this.startListenInternal(roomId, true);
    }

    private boolean startListenInternal(long roomId, boolean external) {
        if (listenerMap.containsKey(roomId)) return false;
        var topic = new ChannelTopic("blive:" + roomId);
        var listener = factory.getBean(BilibiliLiveListener.class);
        redisListener.addMessageListener(listener, topic);
        this.listenerMap.put(roomId, listener);
        if (external) bLiveSettings.listening.add(roomId);
        return true;
    }


    public boolean stopListen(long roomId) {
        var topic = new ChannelTopic("blive:" + roomId);
        var listener = listenerMap.remove(roomId);
        if (listener == null) {
            logger.info("???????????? roomId ??????????????????");
            return false;
        }
        redisListener.removeMessageListener(listener, topic);
        bLiveSettings.listening.remove(roomId);
        return true;
    }


    public Mono<LiveInfo> getRoomInfo(long roomId) {
        if (roomInfo.containsKey(roomId)){
            return Mono.just(roomInfo.get(roomId));
        }
        Mono<LiveInfo> mono = Mono.create(sink -> {
            try {
                URL url = new URL("https://api.live.bilibili.com/room/v1/Room/get_info?room_id=" + roomId);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                InputStream stream = connection.getInputStream();
                LiveInfo info = mapper.readValue(stream, LiveInfo.class);
                this.roomInfo.put(roomId, info);
                sink.success(info);
            } catch (IOException e) {
                logger.error("Error while checking room is valid: ", e);
                sink.error(e);
            }
        });
        return mono.subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<Boolean> isValidRoom(long roomId){
        return this.getRoomInfo(roomId).map(info -> info.code == 0);
    }


    public boolean clearErrorCache(long roomId){
        if (roomId == -1){
            this.roomInfo.clear();
            return true;
        }else{
            return this.roomInfo.remove(roomId) != null;
        }
    }

    public static class LiveInfo {

        public int code;
        public String msg;
        public String message;

    }

}
