package com.ericlam.qqbot.valbot.job;

import com.ericlam.qqbot.valbot.RequestException;
import com.ericlam.qqbot.valbot.dto.ValBotData;
import com.ericlam.qqbot.valbot.dto.res.EssenceInfo;
import com.ericlam.qqbot.valbot.service.QQBotService;
import com.ericlam.qqbot.valbot.service.ValDataService;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.BotContainer;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.text.DateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Component
public class ReferenceEssenceJob implements Job {

    @Autowired
    private QQBotService botService;

    @Autowired
    private BotContainer container;

    @Autowired
    private DateFormat dateFormat;


    private static final Logger LOGGER = LoggerFactory.getLogger(ReferenceEssenceJob.class);

    @Value("${val.bot}")
    private long botId;

    @Value("${val.group}")
    private long groupId;

    private final ValBotData.CommonSettings settings;

    public ReferenceEssenceJob(ValDataService dataService) {
        this.settings = dataService.getData().settings;
    }

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        var now = System.currentTimeMillis();

        if (settings.lastChecked == -1){
            settings.lastChecked = now;
        }else{
            var duration = Duration.between(
                    Instant.ofEpochMilli(settings.lastChecked),
                    Instant.ofEpochMilli(now));
            if (duration.toDays() < 1){
                return;
            }else{
                settings.lastChecked = now;
            }

        }
        LOGGER.debug("正在检查 {} 有无群精华消息被设置...", tellTime());
        var bot = container.robots.get(botId);
        if (bot == null) {
            LOGGER.warn("找不到QQ机器人，已略过。");
            return;
        }
        List<EssenceInfo> list;

        try {
            list = botService.getGroupEssenceMsgList(bot, groupId).getData();
        } catch (RequestException e) {
            LOGGER.warn("请求时出现错误: " + e.getMessage(), e);
            return;
        }

        if (list.isEmpty()){
            LOGGER.debug("无，已略过。");
        }

        Flux.fromIterable(list)
                .filter(info -> toCalender(info.getSenderTime()).get(dayField()) == today())
                .filter(info -> isNotToday(info.getSenderTime())) // not this year / this month
                .mapNotNull(info -> {
                    var data = bot.getMsg(info.getMessageId());
                    if (data.getRetcode() == -1){
                        return null;
                    }else{
                        bot.sendGroupMsg(groupId, MsgUtils
                                .builder()
                                .text(tellTime()).text(",").text("\n")
                                .at(info.getOperatorId())
                                .text("设置了一则由")
                                .at(info.getSenderId())
                                .text("所发送的消息为精华消息: ")
                                .build(), false);
                        return data.getData();
                    }

                })
                .doOnError(ex -> {
                    LOGGER.error("获取消息时出现错误: ", ex);
                    bot.sendGroupMsg(groupId, "...加载失败: " + ex.getMessage(), false);
                })
                .subscribe(msg -> bot.sendGroupMsg(groupId, msg.getRawMessage(), false));
    }

    private String toFormatString(long time) {
        return dateFormat.format(new Date(time * 1000));
    }


    private Calendar toCalender(long time) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date(time * 1000));
        return calendar;
    }

    private int today() {
        return Calendar.getInstance().get(dayField());
    }

    private boolean isNotToday(long time){
        Calendar that = toCalender(time);
        Calendar now = Calendar.getInstance();
        return !(that.get(Calendar.YEAR) == now.get(Calendar.YEAR) &&
                that.get(Calendar.MONTH) == now.get(Calendar.MONTH));

    }

    private int dayField() {
        return settings.yearlyCheck ? Calendar.DAY_OF_YEAR : Calendar.DAY_OF_MONTH;
    }



    private String tellTime() {
        return settings.yearlyCheck ? "上年度的今天" : "上个月的今天";
    }
}
