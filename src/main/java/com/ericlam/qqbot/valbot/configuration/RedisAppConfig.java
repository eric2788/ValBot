package com.ericlam.qqbot.valbot.configuration;

import com.ericlam.qqbot.valbot.crossplatform.livehandle.BiliLiveHandle;
import com.ericlam.qqbot.valbot.crossplatform.livehandle.TweetsHandle;
import com.ericlam.qqbot.valbot.crossplatform.livehandle.YTLiveHandle;
import com.ericlam.qqbot.valbot.crossplatform.subscriber.LiveSubscriber;
import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordLiveSubscriber;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQLiveSubscriber;
import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
import com.ericlam.qqbot.valbot.dto.TweetStreamData;
import com.ericlam.qqbot.valbot.dto.YoutubeLiveInfo;
import com.ericlam.qqbot.valbot.redis.LiveRoomStatusSubscriber;
import com.ericlam.qqbot.valbot.redis.blivehandle.BroadcastHandle;
import com.ericlam.qqbot.valbot.redis.blivehandle.DanmuHandle;
import com.ericlam.qqbot.valbot.redis.blivehandle.RoomEnterHandle;
import com.ericlam.qqbot.valbot.redis.blivehandle.SuperChatHandle;
import com.ericlam.qqbot.valbot.redis.tweethandle.DeleteTweetHandle;
import com.ericlam.qqbot.valbot.redis.tweethandle.NewTweetHandle;
import com.ericlam.qqbot.valbot.redis.tweethandle.RetweetHandle;
import com.ericlam.qqbot.valbot.redis.ythandle.LiveEndHandle;
import com.ericlam.qqbot.valbot.redis.ythandle.LiveStartHandle;
import com.ericlam.qqbot.valbot.redis.ythandle.LiveUpComingHandle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;

import java.util.List;
import java.util.Map;


@ComponentScan("com.ericlam.qqbot.valbot.redis")
@Configuration
public class RedisAppConfig {

    private static final Logger LOG  = LoggerFactory.getLogger(RedisAppConfig.class);


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

    @Bean("bili-live-handle")
    public Map<String, Class<? extends BiliLiveHandle>> biliHandlers(){
        return Map.of(
                BLiveWebSocketData.CommandType.LIVE, BroadcastHandle.class,
                BLiveWebSocketData.CommandType.DANMU_MSG, DanmuHandle.class,
                BLiveWebSocketData.CommandType.SUPER_CHAT_MESSAGE, SuperChatHandle.class,
                BLiveWebSocketData.CommandType.INTERACT_WORD, RoomEnterHandle.class
        );
    }

    @Bean("yt-live-handle")
    public Map<String, Class<? extends YTLiveHandle>> ytHandlers(){
        return Map.of(
                YoutubeLiveInfo.LiveStatus.LIVE, LiveStartHandle.class,
                YoutubeLiveInfo.LiveStatus.IDLE, LiveEndHandle.class,
                YoutubeLiveInfo.LiveStatus.UPCOMING, LiveUpComingHandle.class
        );
    }

    @Bean("tweet-handle")
    public Map<TweetStreamData.Command, Class<? extends TweetsHandle>> tweetHandlers(){
        return Map.of(
                TweetStreamData.Command.TWEET, NewTweetHandle.class,
                TweetStreamData.Command.RETWEET, RetweetHandle.class,
                TweetStreamData.Command.DELETE, DeleteTweetHandle.class
        );
    }

    @Bean("ws-subscribers")
    public List<? extends LiveSubscriber> bLiveSubscribers(BeanFactory factory){
        return List.of(
                factory.getBean(QQLiveSubscriber.class),
                factory.getBean(DiscordLiveSubscriber.class)
        );
    }

}
