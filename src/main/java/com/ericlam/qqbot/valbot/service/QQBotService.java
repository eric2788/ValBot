package com.ericlam.qqbot.valbot.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ericlam.qqbot.valbot.dto.res.EssenceInfo;
import com.ericlam.qqbot.valbot.dto.res.Message;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotContainer;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.common.ActionList;
import com.mikuac.shiro.enums.ActionPathEnum;
import com.mikuac.shiro.handler.ActionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.socket.WebSocketSession;
import sun.misc.Unsafe;
import sun.reflect.ReflectionFactory;

import javax.annotation.Resource;
import java.lang.reflect.*;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;


// extend new feature that shiro doesn't provide
@Service
public class QQBotService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QQBotService.class);

    private static final ActionPathEnum GET_ESSENCE_MSG_LIST = createCustomAction("get_essence_msg_list", "获取精华消息列表");
    private static final ActionPathEnum GET_MSG = createCustomAction("get_msg", "获取消息");


    @Autowired
    private ActionHandler actionHandler;

    public ActionList<EssenceInfo> getGroupEssenceMsgList(Bot bot, long groupId) {
        return this.doRequest(bot, GET_ESSENCE_MSG_LIST, Map.of("group_id", groupId));
    }

    public ActionData<Message> getMessage(Bot bot, int messageId){
        return this.doRequest(bot, GET_MSG, Map.of("message_id", messageId));
    }






    private <T> T doRequest(Bot bot, ActionPathEnum action, Map<String, Object> params){
        var session = bot.getSession();
        JSONObject o = new JSONObject();
        o.putAll(params);
        var result = this.actionHandler.doActionRequest(session, action, o);
        return result != null ? result.toJavaObject(new TypeReference<T>(){}) : null;
    }


    public static ActionPathEnum createCustomAction(String url, String desc) {
        try {
            var con = ActionPathEnum.class.getDeclaredConstructors()[0];
            con.setAccessible(true);
            ReflectionFactory factory = ReflectionFactory.getReflectionFactory();
            var realCon = factory.newConstructorForSerialization(ActionPathEnum.class, con);
            return (ActionPathEnum) realCon.newInstance(url, desc);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }



}
