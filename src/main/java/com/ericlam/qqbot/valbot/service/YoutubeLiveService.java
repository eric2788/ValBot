package com.ericlam.qqbot.valbot.service;

import com.ericlam.qqbot.valbot.RequestException;
import com.ericlam.qqbot.valbot.dto.ValBotData;
import com.ericlam.qqbot.valbot.redis.YoutubeLiveListener;
import org.slf4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class YoutubeLiveService {

    private final ValBotData.YoutubeSettings youtubeSettings;

    @Autowired
    private RedisMessageListenerContainer redisListener;

    @Autowired
    private BeanFactory factory;

    @Autowired
    private Logger logger;

    private final Map<String, String> channelIdCache = new ConcurrentHashMap<>();
    private final Map<String, MessageListener> listenerMap = new HashMap<>();

    private static final Pattern CUSTOM_URL_PATTERN = Pattern.compile("(https?:\\/\\/)?(www\\.)?youtube\\.com\\/c\\/([\\w-]+)");
    private static final Pattern CHANNEL_URL_PATTERN = Pattern.compile("(https?:\\/\\/)?(www\\.)?youtube\\.com\\/(channel|user)\\/([\\w-]+)");

    private static Pattern getChannelPattern(String username) {
        return Pattern.compile("\"browseId\":\"([\\w-]+)\",\"canonicalBaseUrl\":\"\\/c\\/" + username + "\"");
    }

    public YoutubeLiveService(ValDataService dataService) {
        this.youtubeSettings = dataService.getData().youtubeSettings;
    }

    @PostConstruct
    public void onCreate() {
        for (String toListen : Set.copyOf(youtubeSettings.listening)) {
            this.startListenInternal(toListen, false);
        }
    }

    public Set<String> getChannelListening() {
        return listenerMap.keySet();
    }

    private boolean startListenInternal(String channel, boolean external) {
        if (listenerMap.containsKey(channel)) return false;
        var topic = new ChannelTopic("ylive:" + channel);
        var listener = factory.getBean(YoutubeLiveListener.class);
        redisListener.addMessageListener(listener, topic);
        this.listenerMap.put(channel, listener);
        if (external) youtubeSettings.listening.add(channel);
        return true;
    }

    public boolean startListen(String channel) {
        return this.startListenInternal(channel, true);
    }

    public boolean stopListen(String channel) {
        var topic = new ChannelTopic("ylive:" + channel);
        var listener = listenerMap.remove(channel);
        if (listener == null) {
            logger.info("找不到此 channel 所屬監聽器。");
            return false;
        }
        redisListener.removeMessageListener(listener, topic);
        youtubeSettings.listening.remove(channel);
        return true;
    }

    public CompletableFuture<String> getChannel(String channelUrl) {
        Matcher channelFind = CHANNEL_URL_PATTERN.matcher(channelUrl);
        if (channelFind.find()) {
            return CompletableFuture.completedFuture(channelFind.group(1));
        }
        Matcher customUrlFind = CUSTOM_URL_PATTERN.matcher(channelUrl);
        if (!customUrlFind.find()) {
            return CompletableFuture.completedFuture(null);
        }
        String username = customUrlFind.group(1);
        if (this.channelIdCache.containsKey(username)){
            return CompletableFuture.completedFuture(this.channelIdCache.get(username));
        }
        Pattern pattern = getChannelPattern(username);
        return CompletableFuture.supplyAsync(() -> {
            try {
                URL url = new URL(channelUrl);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setRequestProperty("Content-Type", "application/json");
                InputStream stream = connection.getInputStream();
                String content = new String(stream.readAllBytes());
                Matcher matcher = pattern.matcher(content);
                if (!matcher.find()){
                    return null;
                }else{
                    String channelId = matcher.group(1);
                    this.channelIdCache.put(username, channelId);
                    return channelId;
                }
            } catch (IOException e) {
                logger.error("Error while parsing custom url: ", e);
                throw new RequestException(e.getMessage());
            }
        });
    }
}
