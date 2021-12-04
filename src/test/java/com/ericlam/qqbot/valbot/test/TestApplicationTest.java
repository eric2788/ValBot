package com.ericlam.qqbot.valbot.test;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.ericlam.qqbot.valbot.filter.ForwardToDiscordFilter;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.dto.action.common.ActionList;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;

class TestApplicationTest {

    void convertToActionList() {
        String json = "{\"data\":[1,2,3],\"echo\":1,\"retcode\":0,\"status\":\"ok\"}";
        JSONObject o = (JSONObject) JSONObject.parse(json);
        System.out.println(o.get("data"));
        ActionList<Integer> obj = toObject(o); // can't
        obj.getData().forEach(e -> System.out.println(e.getClass()));
    }


    public <T> T toObject(JSONObject o){
        return o.toJavaObject(new TypeReference<T>(){});
    }


    @Test
    void testDuration() throws InterruptedException {
        var prev = System.currentTimeMillis();
        Thread.sleep(5000);
        var now = System.currentTimeMillis();
        var duration = Duration.between(
                Instant.ofEpochMilli(prev),
                Instant.ofEpochMilli(now));
        System.out.println(duration.getSeconds());
        duration = Duration.between(
                Instant.ofEpochMilli(now),
                Instant.ofEpochMilli(prev)
        );
        System.out.println(duration.getSeconds());
    }

    void parseDate() throws ParseException {
        String time = "Mon Oct 04 17:05:00 +0000 2021";
        var d = DateTimeFormatter.ofPattern("E MMM dd HH:mm:ss +zzzz uuuu");
        System.out.println(d.parse(time));
    }

    @Getter
    @Setter
    public static class MyActionList<T> extends ActionList<T> {

        @JSONField(name = "echo")
        private int echo;

    }
}