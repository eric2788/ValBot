package com.ericlam.qqbot.valbot.test;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.annotation.JSONField;
import com.mikuac.shiro.dto.action.common.ActionList;
import lombok.Getter;
import lombok.Setter;
import org.junit.jupiter.api.Test;

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


    @Getter
    @Setter
    public static class MyActionList<T> extends ActionList<T> {

        @JSONField(name = "echo")
        private int echo;

    }
}