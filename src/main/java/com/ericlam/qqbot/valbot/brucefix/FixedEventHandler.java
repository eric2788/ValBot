package com.ericlam.qqbot.valbot.brucefix;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.annotation.JSONField;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.request.FriendAddRequestEvent;
import com.mikuac.shiro.dto.event.request.GroupAddRequestEvent;
import com.mikuac.shiro.handler.EventHandler;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Iterator;

// 强行 override 修复
@Primary
@Component
public class FixedEventHandler extends EventHandler {

    private static final Logger Log = LoggerFactory.getLogger(FixedEventHandler.class);

    @Resource
    private ApplicationContext applicationContext;

    private final BotPlugin defaultPlugin = new BotPlugin();

    @Override
    public void handle(Bot bot, JSONObject eventJson) {
        String postType = eventJson.getString("post_type");
        if (postType.equals("request")) { // override request handle
            this.handleRequest(bot, eventJson);
            return;
        }
        super.handle(bot, eventJson);
    }

    private void handleRequest(Bot bot, JSONObject eventJson) {
        Log.debug("现在正使用自定义处理器 处理请求事件。");
        String requestType = eventJson.getString("request_type");

        Iterator<Class<? extends BotPlugin>> botPluginIterator;
        Class<? extends BotPlugin> botPlugin;
        switch (requestType) {
            case "friend" -> {
                FixedFriendAddRequestEvent event = eventJson.toJavaObject(FixedFriendAddRequestEvent.class);
                botPluginIterator = bot.getPluginList().iterator();
                do {
                    if (!botPluginIterator.hasNext()) {
                        return;
                    }

                    botPlugin = botPluginIterator.next();
                } while (this.getPlugin(botPlugin).onFriendAddRequest(bot, event) != 1);
            }
            case "group" -> {
                GroupAddRequestEvent event = eventJson.toJavaObject(GroupAddRequestEvent.class);
                botPluginIterator = bot.getPluginList().iterator();
                do {
                    if (!botPluginIterator.hasNext()) {
                        return;
                    }

                    botPlugin = botPluginIterator.next();
                } while (this.getPlugin(botPlugin).onGroupAddRequest(bot, event) != 1);
            }
            default -> {
            }
        }
    }

    private BotPlugin getPlugin(Class<? extends BotPlugin> pluginClass) {
        try {
            return this.applicationContext.getBean(pluginClass);
        } catch (Exception var3) {
            Log.warn("插件 {} 已被跳过，请检查 @Component 注解", pluginClass.getSimpleName());
            return this.defaultPlugin;
        }
    }

    @Getter
    @Setter
    public static class FixedFriendAddRequestEvent extends FriendAddRequestEvent {

        @JSONField(
                name = "request_type"
        )
        private String requestType;
        @JSONField(
                name = "user_id"
        )
        private long userId;
        @JSONField(
                name = "comment"
        )
        private String comment;
        @JSONField(
                name = "flag"
        )
        private String flag;

    }

}
