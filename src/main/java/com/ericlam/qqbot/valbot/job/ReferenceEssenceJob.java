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
import java.util.Optional;

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
        LOGGER.debug("???????????? {} ??????????????????????????????...", tellTime());
        var bot = container.robots.get(botId);
        if (bot == null) {
            LOGGER.warn("?????????QQ????????????????????????");
            return;
        }
        List<EssenceInfo> list;

        try {
            list = botService.getGroupEssenceMsgList(bot, groupId).getData();
        } catch (RequestException e) {
            LOGGER.warn("?????????????????????: " + e.getMessage(), e);
            return;
        }

        if (list.isEmpty()){
            LOGGER.debug("??????????????????");
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
                                .text("??????????????????")
                                .at(info.getSenderId())
                                .text("?????????????????????????????????: ")
                                .build(), false);
                        return data.getData();
                    }

                })
                .doOnError(ex -> {
                    LOGGER.error("???????????????????????????: ", ex);
                    bot.sendGroupMsg(groupId, "...????????????: " + ex.getMessage(), false);
                })
                .subscribe(message -> {
                    var msg = Optional.ofNullable(message.getRawMessage()).orElseGet(message::getMessage);
                    bot.sendGroupMsg(groupId, msg, false);
                });
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
        return settings.yearlyCheck ? "??????????????????" : "??????????????????";
    }
}
