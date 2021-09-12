package com.ericlam.qqbot.valbot.redis;

import com.ericlam.qqbot.valbot.crossplatform.BLiveHandle;
import com.ericlam.qqbot.valbot.crossplatform.BLiveSubscriber;
import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
import com.ericlam.qqbot.valbot.service.ValDataService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import javax.annotation.Nonnull;
import javax.annotation.Resource;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
public class BilibiliLiveSubscriber implements MessageListener {

    private static final Set<String> exceptionCommands = new HashSet<>();

    @Autowired
    private ValDataService dataService;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private Logger logger;

    @Autowired
    private BeanFactory beanFactory;

    @Resource(name = "ws-handler")
    private Map<String, Class<? extends BLiveHandle>> handlerMap;

    @Resource(name = "ws-subscribers")
    private List<? extends BLiveSubscriber> bLiveSubscribers;



    @Override
    public void onMessage(@Nonnull Message message, byte[] bytes) {
        String roomIdStr = new String(message.getChannel()).replace("blive:", "");
        long room;
        try {
            room = Long.parseLong(roomIdStr);
        } catch (NumberFormatException e) {
            logger.warn("無法解析頻道 room id");
            return;
        }
        try {
            var ws = mapper.readValue(message.getBody(), BLiveWebSocketData.class);
            if (exceptionCommands.contains(ws.command)) return;
            var handleCls = handlerMap.get(ws.command);
            if (handleCls == null){
                logger.debug("找不到 指令 {} 的處理方法，已略過。", ws.command);
                exceptionCommands.add(ws.command);
                return;
            }
            //logger.debug("(房间{}) 收到WS指令: {}", room, ws); // too spam
            this.handleWSLiveData(handleCls, room, ws);
        } catch (IOException e) {
            if (dataService.getData().bLiveSettings.verbose){
                bLiveSubscribers.forEach(sub -> sub.doOnError(e, room));
            }
            logger.warn("Error while parsing data ", e);
        }
    }

    // you can make fake data
    public <T extends BLiveHandle> void handleWSLiveData(Class<T> handleCls, long room, BLiveWebSocketData ws) throws IOException{
        var handle = beanFactory.getBean(handleCls);
        for (BLiveSubscriber subscriber : this.bLiveSubscribers) {
            subscriber.subscribe(handle, room, ws);
        }
    }

}
