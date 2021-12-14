package com.ericlam.qqbot.valbot.service;

import com.alibaba.fastjson.annotation.JSONField;
import com.ericlam.qqbot.valbot.dto.ValBotData;
import com.ericlam.qqbot.valbot.redis.TwitterListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import javax.annotation.PostConstruct;
import javax.imageio.plugins.tiff.ExifGPSTagSet;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TwitterService {

    @Autowired
    private RedisMessageListenerContainer redisListener;

    @Autowired
    private BeanFactory factory;

    @Autowired
    private Logger logger;

    @Value("${twitter.userlookup.url}")
    private String apiUrl;


    @Autowired
    private ObjectMapper mapper;

    private final Map<String, ExistUserResponse> userExistInfo = new ConcurrentHashMap<>();
    private final Map<String, MessageListener> listenerMap = new HashMap<>();

    private final ValBotData.TwitterSettings twitterSettings;

    public TwitterService(ValDataService dataService) {
        this.twitterSettings = dataService.getData().twitterSettings;
    }

    public Set<String> getLiveRoomListening() {
        return listenerMap.keySet();
    }

    @PostConstruct
    public void onCreate() {
        for (String toListen : Set.copyOf(twitterSettings.listening)) {
            this.startListenInternal(toListen, false);
        }
    }

    public boolean startListen(String username) {
        return this.startListenInternal(username, true);
    }

    private boolean startListenInternal(String username, boolean external) {
        if (listenerMap.containsKey(username)) return false;
        var topic = new ChannelTopic("twitter:" + username);
        var listener = factory.getBean(TwitterListener.class);
        redisListener.addMessageListener(listener, topic);
        this.listenerMap.put(username, listener);
        if (external) twitterSettings.listening.add(username);
        return true;
    }


    public boolean stopListen(String username) {
        var topic = new ChannelTopic("twitter:" + username);
        var listener = listenerMap.remove(username);
        if (listener == null) {
            logger.info("找不到此 twitter用戶 所屬監聽器。");
            return false;
        }
        redisListener.removeMessageListener(listener, topic);
        twitterSettings.listening.remove(username);
        return true;
    }

    public Mono<ExistUserResponse> fetchTwitterUser(String username) {
        if (userExistInfo.containsKey(username)) {
            return Mono.just(userExistInfo.get(username));
        }
        Mono<ExistUserResponse> mono = Mono.create(sink -> {
            try {
                URL url = new URL(MessageFormat.format("{0}/userExist/{1}", apiUrl, username));
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                if (connection.getResponseCode() == 200) {
                    InputStream stream = connection.getInputStream();
                    ExistUserResponse response = mapper.readValue(stream, ExistUserResponse.class);
                    userExistInfo.put(username, response);
                    sink.success(response);
                }else if (connection.getResponseCode() == 400) {
                    InputStream stream = connection.getErrorStream();
                    sink.error(new RuntimeException(mapper.readValue(stream, ErrorResponse.class).error[0]));
                }
            } catch (IOException e) {
                logger.error("Error while checking room is valid: ", e);
                sink.error(e);
            }
        });
        return mono.subscribeOn(Schedulers.boundedElastic());
    }

    public static class ErrorResponse {
        public String[] error;
    }

    public static class ExistUserResponse {

        public boolean exist;
        public ProfileData data;

        public static class ProfileData {

            public int id;
            public String name;
            public String username;
        }
    }

    @Deprecated // no longer exist
    public static class ShadowBanResponse {

        public double timestamp;
        public Profile profile;

        public static class Profile {

            @JSONField(name = "protected")
            public boolean is_protected;
            public boolean exists;
            public boolean has_tweets;
            public String screen_name;

        }
    }
}
