package com.ericlam.qqbot.valbot.redis;

import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
import com.ericlam.qqbot.valbot.redis.wshandle.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisClusterConfiguration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.Map;


@ComponentScan("com.ericlam.qqbot.valbot.redis")
@Configuration
@EnableAutoConfiguration
public class RedisConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory(
            @Value("${spring.redis.host}") String host,
            @Value("${spring.redis.port}") int port,
            @Value("${spring.redis.database}") int db
    ) {
        var config = new RedisStandaloneConfiguration(host, port);
        config.setDatabase(db);
        return new LettuceConnectionFactory(config);
    }


    @Bean
    public RedisMessageListenerContainer redisMessageListenerContainer(RedisConnectionFactory factory, LiveRoomStatusSubscriber subscriber) {
        RedisMessageListenerContainer container = new RedisMessageListenerContainer();
        container.setConnectionFactory(factory);
        container.addMessageListener(subscriber, new ChannelTopic("live-room-status"));
        return container;
    }

    @Bean
    public RedisTemplate<String, Integer> redisTemplate(RedisConnectionFactory factory) {
        RedisTemplate<String, Integer> template = new RedisTemplate<>();
        template.setConnectionFactory(factory);
        return template;
    }

    @Bean("ws-handler")
    public Map<String, Class<? extends BLiveHandle>> wsHandler(){
        return Map.of(
                BLiveWebSocketData.CommandType.LIVE, BroadcastHandle.class,
                BLiveWebSocketData.CommandType.DANMU_MSG, DanmuHandle.class,
                BLiveWebSocketData.CommandType.SUPER_CHAT_MESSAGE, SuperChatHandle.class,
                BLiveWebSocketData.CommandType.INTERACT_WORD, RoomEnterHandle.class
        );
    }

}
