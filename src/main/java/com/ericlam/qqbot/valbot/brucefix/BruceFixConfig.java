package com.ericlam.qqbot.valbot.brucefix;

import com.mikuac.shiro.handler.EventHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BruceFixConfig {

    private static final Logger log = LoggerFactory.getLogger(BruceFixConfig.class);

    @Bean
    public EventHandler eventHandler(EventHandler handler){
        if (handler instanceof FixedEventHandler){
            log.info("正在使用自定义事件处理器");
        }else{
            log.warn("正在使用原版事件处理器");
        }
        return handler;
    }
}
