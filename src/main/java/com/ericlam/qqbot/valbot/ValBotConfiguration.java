package com.ericlam.qqbot.valbot;

import com.ericlam.qqbot.valbot.command.CheckCommand;
import com.ericlam.qqbot.valbot.command.GroupChatCommand;
import com.ericlam.qqbot.valbot.command.SpeakCommand;
import com.ericlam.qqbot.valbot.command.response.CustomResponseCommand;
import com.ericlam.qqbot.valbot.command.yesno.YesNoCommand;
import com.ericlam.qqbot.valbot.response.ChatResponse;
import com.ericlam.qqbot.valbot.response.GroupChatResponse;
import com.ericlam.qqbot.valbot.response.PrivateChatResponse;
import com.ericlam.qqbot.valbot.response.group.CustomResponse;
import com.ericlam.qqbot.valbot.response.group.DaCallResponse;
import com.ericlam.qqbot.valbot.response.group.RepeatResponse;
import com.ericlam.qqbot.valbot.response.group.YesNoResponse;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import com.mikuac.shiro.dto.event.message.PrivateMessageEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.List;


@EnableScheduling
@Configuration
@EnableAutoConfiguration
public class ValBotConfiguration {

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

    public static class ValBot {
    }


    // register section

    @Bean("group-chat-responses")
    public List<Class<? extends GroupChatResponse>> groupChatResponses() {
        return List.of(
                RepeatResponse.class,
                YesNoResponse.class,
                CustomResponse.class,
                DaCallResponse.class
        );
    }

    @Bean("private-chat-responses")
    public List<Class<? extends PrivateChatResponse>> privateChatResponses() {
        return List.of();
    }


    @Bean("commands")
    public List<Class<? extends GroupChatCommand>> chatCommands(){
        return List.of(
                SpeakCommand.class,
                YesNoCommand.class,
                CustomResponseCommand.class,
                CheckCommand.class
        );
    }


}
