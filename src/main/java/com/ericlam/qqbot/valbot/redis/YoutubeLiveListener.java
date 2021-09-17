package com.ericlam.qqbot.valbot.redis;

import com.ericlam.qqbot.valbot.crossplatform.livehandle.YTLiveHandle;
import com.ericlam.qqbot.valbot.crossplatform.subscriber.BiliLiveSubscriber;
import com.ericlam.qqbot.valbot.crossplatform.subscriber.LiveSubscriber;
import com.ericlam.qqbot.valbot.crossplatform.subscriber.YTLiveSubscriber;
import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
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
public class YoutubeLiveListener implements MessageListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(YoutubeLiveListener.class);

    private static final Set<String> exceptionStatus = new HashSet<>();

    @Autowired
    private ObjectMapper mapper;

    @Resource(name = "yt-live-handle")
    private Map<String, Class<? extends YTLiveHandle>> handlers;

    @Resource(name = "ws-subscribers")
    private List<? extends LiveSubscriber> bLiveSubscribers;

    @Autowired
    private ValDataService dataService;

    @Autowired
    private BeanFactory beanFactory;


    @Override
    public void onMessage(@NotNull Message message, byte[] bytes) {
        String youtubeChannel = new String(message.getChannel()).replace("ylive:", "");
        if (!youtubeChannel.startsWith("UC")) {
            LOGGER.warn("无法解析频道 ID {}", youtubeChannel);
            return;
        }

        try {

            var info = mapper.readValue(message.getBody(), YoutubeLiveInfo.class);
            if (exceptionStatus.contains(info.status)) return;
            var handler = handlers.get(info.status);
            if (handler == null) {
                LOGGER.warn("未知的状态 {}, 已略过。", info.status);
                exceptionStatus.add(info.status);
                return;
            }
            LOGGER.debug("收到从频道 {} 的指令: {}", youtubeChannel, info);
            this.handleLiveData(handler, youtubeChannel, info);
        } catch (IOException e) {
            if (dataService.getData().settings.verbose) {
                bLiveSubscribers.forEach(sub -> sub.doOnError(e, youtubeChannel));
            }
            LOGGER.warn("Error while parsing data ", e);
        }
    }

    // you can make fake data
    public <T extends YTLiveHandle> void handleLiveData(Class<T> handleCls, String channelId, YoutubeLiveInfo info) throws IOException {
        var handle = beanFactory.getBean(handleCls);
        for (LiveSubscriber subscriber : this.bLiveSubscribers) {
            if (subscriber instanceof YTLiveSubscriber yt) {
                yt.subscribe(handle, channelId, info);
            }
        }
    }
}
