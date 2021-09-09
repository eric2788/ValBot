package com.ericlam.qqbot.valbot.configuration;

import com.ericlam.qqbot.valbot.crossplatform.BLiveHandle;
import com.ericlam.qqbot.valbot.crossplatform.BLiveSubscriber;
import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordBLiveSubscriber;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQBLiveSubscriber;
import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
import com.ericlam.qqbot.valbot.redis.LiveRoomStatusSubscriber;
import com.ericlam.qqbot.valbot.redis.wshandle.BroadcastHandle;
import com.ericlam.qqbot.valbot.redis.wshandle.DanmuHandle;
import com.ericlam.qqbot.valbot.redis.wshandle.RoomEnterHandle;
import com.ericlam.qqbot.valbot.redis.wshandle.SuperChatHandle;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.List;
import java.util.Map;


@ComponentScan("com.ericlam.qqbot.valbot.redis")
@Configuration
public class RedisAppConfig {

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory();
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

    @Bean("ws-subscribers")
    public List<? extends BLiveSubscriber> bLiveSubscribers(BeanFactory factory){
        return List.of(
                factory.getBean(QQBLiveSubscriber.class),
                factory.getBean(DiscordBLiveSubscriber.class)
        );
    }

}
