package com.ericlam.qqbot.valbot.service;

import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.ericlam.qqbot.valbot.RequestException;
import com.ericlam.qqbot.valbot.dto.CustomRequest;
import com.ericlam.qqbot.valbot.dto.res.EssenceInfo;
import com.ericlam.qqbot.valbot.dto.res.ForwardedContent;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.action.common.ActionData;
import com.mikuac.shiro.dto.action.common.ActionList;
import com.mikuac.shiro.enums.ActionPath;
import com.mikuac.shiro.handler.ActionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Map;


// extend new feature that shiro doesn't provide
@Service
public class QQBotService {

    private static final Logger LOGGER = LoggerFactory.getLogger(QQBotService.class);

    private static final ActionPath GET_ESSENCE_MSG_LIST = new CustomRequest("get_essence_msg_list");
    private static final ActionPath GET_FORWARD_MSG = new CustomRequest("get_forward_msg");

    @Autowired
    private ActionHandler actionHandler;

    public ActionList<EssenceInfo> getGroupEssenceMsgList(Bot bot, long groupId) {
        return this.doRequest(bot, GET_ESSENCE_MSG_LIST, Map.of("group_id", groupId));
    }

    public ActionData<ForwardedContent> getForwardMsg(Bot bot, String msgId){
        return this.doRequest(bot, GET_FORWARD_MSG, Map.of("message_id", msgId));
    }


    public <T> T validateNotError(T obj){
        if (obj == null) throw new RequestException("触发限流策略或WS session没有开启");
       try {
           var getRetCode = obj.getClass().getMethod("getRetCode");
           var getStatus = obj.getClass().getMethod("getStatus");
           var retCode = (Integer)getRetCode.invoke(obj);
           var status = (String) getStatus.invoke(obj);
           if (retCode == -1 || status.equals("failed")) throw new RequestException("网络请求失败。");
       }catch (Exception e){
           throw new RequestException("其他错误: "+e.getMessage());
       }
       return obj;
    }


    public  <T> T doRequest(Bot bot, ActionPath action, Map<String, Object> params) {
        var session = bot.getSession();
        JSONObject o = new JSONObject();
        o.putAll(params);
        var result = this.actionHandler.doActionRequest(session, action, o);
        if (result == null) throw new RequestException("触发限流策略或WS session没有开启");
        if (result.getString("status").equals("failed") || result.getInteger("retcode") == -1){
            throw new RequestException("网络请求失败。");
        }
        return result.toJavaObject(new TypeReference<T>() {
        });
    }







}
