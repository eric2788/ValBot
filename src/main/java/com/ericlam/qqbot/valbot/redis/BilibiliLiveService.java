package com.ericlam.qqbot.valbot.redis;

import org.slf4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.connection.ReactiveRedisConnection;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.ReactiveRedisMessageListenerContainer;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class BilibiliLiveService {

    @Autowired
    private RedisTemplate<String, Integer> redisTemplate;

    @Autowired
    private RedisMessageListenerContainer redisListener;

    @Autowired
    private BeanFactory factory;

    @Autowired
    private Logger logger;

    private final Map<Long, MessageListener> listenerMap = new HashMap<>();

    public Set<Long> getLiveRoomListening() {
        return Optional.ofNullable(redisTemplate.opsForSet()
                .members("live_room_listening"))
                .map(s -> s.stream()
                        .map(Integer::longValue)
                        .collect(Collectors.toSet())).orElse(Set.of());
    }


    public boolean startListen(long roomId) {
        if (listenerMap.containsKey(roomId)) return false;
        var topic =  new ChannelTopic("blive:" + roomId);
        var listener = factory.getBean(BilibiliLiveSubscriber.class);
        redisListener.addMessageListener(listener, topic);
        this.listenerMap.put(roomId, listener);
        return true;
    }


    public void stopListen(long roomId) {
        var topic =  new ChannelTopic("blive:" + roomId);
        var listener = listenerMap.remove(roomId);
        if (listener == null) {
            logger.info("找不到此 roomId 所屬監聽器。");
        }
        redisListener.removeMessageListener(listener, topic);
    }
}
