package com.ericlam.qqbot.valbot.redis;

import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;

public abstract class BotMessageListener implements MessageListener {

    @Autowired
    private BotContainer container;

    @Value("${val.bot}")
    private long botId;

    @Override
    public void onMessage(@NotNull Message message, byte[] bytes) {
        var bot = container.robots.get(botId);
        if (bot == null) return;
        this.onRedisMessage(bot, message, bytes);
    }

    public abstract void onRedisMessage(Bot bot, @NotNull Message message, byte[] bytes);

}
