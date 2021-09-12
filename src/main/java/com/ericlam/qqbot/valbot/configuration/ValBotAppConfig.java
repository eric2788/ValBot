package com.ericlam.qqbot.valbot.configuration;

import com.ericlam.qqbot.valbot.command.CheckCommand;
import com.ericlam.qqbot.valbot.command.SpeakCommand;
import com.ericlam.qqbot.valbot.command.VoiceCommand;
import com.ericlam.qqbot.valbot.command.random.RandomCommand;
import com.ericlam.qqbot.valbot.command.live.BLiveCommand;
import com.ericlam.qqbot.valbot.command.response.CustomResponseCommand;
import com.ericlam.qqbot.valbot.command.settings.SettingCommand;
import com.ericlam.qqbot.valbot.command.test.TestCommand;
import com.ericlam.qqbot.valbot.command.yesno.YesNoCommand;
import com.ericlam.qqbot.valbot.crossplatform.ChatResponse;
import com.ericlam.qqbot.valbot.crossplatform.GroupCommand;
import com.ericlam.qqbot.valbot.job.ReferenceEssenceJob;
import com.ericlam.qqbot.valbot.response.*;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InjectionPoint;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;

import javax.annotation.PostConstruct;
import java.io.File;
import java.text.DateFormat;
import java.util.List;
import java.util.Locale;
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
    public Map<String, String> translateStatus() {
        return Map.of(
                "started", "的监听初始化成功",
                "stopped", "的监听已关闭",
                "existed", "的监听已经开始",
                "server-closed", "已关闭",
                "server-started", "已启动"
        );
    }

    @Bean
    public JobDetail referenceEssence(){
        return JobBuilder.newJob()
                .ofType(ReferenceEssenceJob.class)
                .storeDurably()
                .withIdentity(ReferenceEssenceJob.class.getSimpleName())
                .withDescription("检查上年/上月的同一天所设置的精华消息并鞭尸")
                .build();
    }

    @Bean
    public Trigger referenceEssencePerDay(){
        return TriggerBuilder.newTrigger()
                .forJob(referenceEssence())
                .withIdentity("referenceEssence-per-day")
                .withDescription("do it per day")
                .withSchedule(SimpleScheduleBuilder.repeatHourlyForever(24))
                .build();
    }

    @Bean
    public Scheduler scheduler(SchedulerFactoryBean factoryBean) throws SchedulerException{
        Scheduler scheduler = factoryBean.getScheduler();
        if (scheduler.isStarted()) return scheduler;
        scheduler.start();
        var refEss = referenceEssencePerDay();
        if (!scheduler.checkExists(refEss.getJobKey())){
            scheduler.scheduleJob(referenceEssence(), refEss);
        }
        return scheduler;
    }

    @Bean
    public DateFormat dateFormat(){
        return DateFormat.getDateTimeInstance(2, 2, Locale.SIMPLIFIED_CHINESE);
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
                DaCallResponse.class,
                CanIResponse.class,
                AtResponse.class,
                LaiDianResponse.class
        );
    }


    @Bean("commands")
    public List<Class<? extends GroupCommand>> chatCommands() {
        return List.of(
                SpeakCommand.class,
                YesNoCommand.class,
                CustomResponseCommand.class,
                CheckCommand.class,
                BLiveCommand.class,
                TestCommand.class,
                VoiceCommand.class,
                RandomCommand.class,
                SettingCommand.class
        );
    }


}
