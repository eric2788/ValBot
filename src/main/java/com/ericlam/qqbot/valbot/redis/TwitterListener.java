package com.ericlam.qqbot.valbot.redis;

import com.ericlam.qqbot.valbot.crossplatform.livehandle.TweetsHandle;
import com.ericlam.qqbot.valbot.crossplatform.livehandle.YTLiveHandle;
import com.ericlam.qqbot.valbot.crossplatform.subscriber.LiveSubscriber;
import com.ericlam.qqbot.valbot.crossplatform.subscriber.TwitterSubscriber;
import com.ericlam.qqbot.valbot.crossplatform.subscriber.YTLiveSubscriber;
import com.ericlam.qqbot.valbot.dto.TweetStreamData;
import com.ericlam.qqbot.valbot.dto.YoutubeLiveInfo;
import com.ericlam.qqbot.valbot.service.ValDataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
public class TwitterListener implements MessageListener {

    private static final Set<TweetStreamData.Command> exceptionCommands = new HashSet<>();

    private static final Logger LOGGER = LoggerFactory.getLogger(TwitterListener.class);

    @Autowired
    private ObjectMapper mapper;

    @Resource(name = "ws-subscribers")
    private List<? extends LiveSubscriber> bLiveSubscribers;

    @Resource(name = "tweet-handle")
    private Map<TweetStreamData.Command, Class<? extends TweetsHandle>> tweetHandlers;

    @Autowired
    private ValDataService dataService;

    @Autowired
    private BeanFactory beanFactory;

    @Override
    public void onMessage(@NotNull Message message, byte[] bytes) {
        String username = new String(message.getChannel()).replace("twitter:", "");
        try {
            var data = mapper.readValue(message.getBody(), TweetStreamData.class);
            TweetStreamData.Command status = data.getCommand();
            if (exceptionCommands.contains(status)) return;
            Class<? extends TweetsHandle> handle = tweetHandlers.get(status);
            if (handle == null){
                LOGGER.warn("無效的指令: {}, 已略過。", status);
                exceptionCommands.add(status);
                return;
            }
            LOGGER.debug("收到从推特用户 {} 的新指令: {}", username, status);
            this.handleLiveData(handle, username, data);
        }catch (IOException e){
            if (dataService.getData().settings.verbose) {
                bLiveSubscribers.forEach(sub -> sub.doOnError(e, username));
            }
            LOGGER.warn("Error while parsing data ", e);
        }
    }

    // you can make fake data
    public <T extends TweetsHandle> void handleLiveData(Class<T> handleCls, String username, TweetStreamData data) throws IOException {
        var handle = beanFactory.getBean(handleCls);
        for (LiveSubscriber subscriber : this.bLiveSubscribers) {
            if (subscriber instanceof TwitterSubscriber tw) {
                tw.subscribe(handle, username, data);
            }
        }
    }
}
