package com.ericlam.qqbot.valbot.configuration;

import com.ericlam.qqbot.valbot.command.CheckCommand;
import com.ericlam.qqbot.valbot.command.SpeakCommand;
import com.ericlam.qqbot.valbot.command.live.BLiveCommand;
import com.ericlam.qqbot.valbot.command.response.CustomResponseCommand;
import com.ericlam.qqbot.valbot.command.test.TestCommand;
import com.ericlam.qqbot.valbot.command.yesno.YesNoCommand;
import com.ericlam.qqbot.valbot.crossplatform.ChatResponse;
import com.ericlam.qqbot.valbot.crossplatform.GroupCommand;
import com.ericlam.qqbot.valbot.response.CustomResponse;
import com.ericlam.qqbot.valbot.response.DaCallResponse;
import com.ericlam.qqbot.valbot.response.RepeatResponse;
import com.ericlam.qqbot.valbot.response.YesNoResponse;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;
import java.util.Map;


@EnableScheduling
@Configuration
public class ValBotAppConfig {

    private final File folder = new File("data");

    @PostConstruct
    public void onCreate() {
        if (!folder.exists() && folder.mkdirs()) {
            LoggerFactory.getLogger(this.getClass()).info("Data folder created.");
        }
    }

    @Bean("data")
    public File folder() {
        return folder;
    }

    @Bean
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    public Logger getLogger(InjectionPoint point) {
        Class<?> loggerClass = point.getMethodParameter() == null ? ValBot.class : point.getMethodParameter().getContainingClass();
        return LoggerFactory.getLogger(loggerClass);
    }

    @Bean
    public ObjectMapper getMapper() {
        return new ObjectMapper()
                .disable(DeserializationFeature.FAIL_ON_IGNORED_PROPERTIES)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .enable(JsonParser.Feature.ALLOW_COMMENTS)
                .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                .disable(SerializationFeature.FAIL_ON_EMPTY_BEANS)
                .setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .setSerializationInclusion(JsonInclude.Include.NON_NULL);
    }

    @Bean("translate-status")
    public Map<String, String> translateStatus(){
        return Map.of(
                "started", "的监听初始化成功",
                "stopped", "的监听已关闭",
                "existed", "的监听已经开始",
                "server-closed", "已关闭",
                "server-started", "已启动"
        );
    }

    public static class ValBot {
    }


    // register section

    @Bean("chat-responses")
    public List<Class<? extends ChatResponse>> groupChatResponses() {
        return List.of(
                RepeatResponse.class,
                YesNoResponse.class,
                CustomResponse.class,
                DaCallResponse.class
        );
    }



    @Bean("commands")
    public List<Class<? extends GroupCommand>> chatCommands(){
        return List.of(
                SpeakCommand.class,
                YesNoCommand.class,
                CustomResponseCommand.class,
                CheckCommand.class,
                BLiveCommand.class,
                TestCommand.class
        );
    }


}
