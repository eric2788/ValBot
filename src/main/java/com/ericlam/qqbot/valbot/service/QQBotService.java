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
import com.mikuac.shiro.dto.action.common.ActionRaw;
import com.mikuac.shiro.enums.ActionPath;
import com.mikuac.shiro.handler.ActionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
        return this.doRequest(bot, GET_ESSENCE_MSG_LIST, Map.of("group_id", groupId), new TypeReference<ActionList<EssenceInfo>>() {
        });
    }

    public ActionData<ForwardedContent> getForwardMsg(Bot bot, String msgId) {
        return this.doRequest(bot, GET_FORWARD_MSG, Map.of("message_id", msgId), new TypeReference<ActionData<ForwardedContent>>() {
        });
    }


    public <T> ActionList<T> validateNotError(ActionList<T> obj) {
        if (obj == null) throw new RequestException("触发限流策略或WS session没有开启");
        var retCode = obj.getRetcode();
        var status = obj.getStatus();
        if (retCode == -1 || status.equals("failed")) throw new RequestException("请求失败。");
        return obj;
    }

    public <T> ActionData<T> validateNotError(ActionData<T> obj) {
        if (obj == null) throw new RequestException("触发限流策略或WS session没有开启");
        var retCode = obj.getRetcode();
        var status = obj.getStatus();
        if (retCode == -1 || status.equals("failed")) throw new RequestException("请求失败。");
        return obj;
    }

    public ActionRaw validateNotError(ActionRaw obj) {
        if (obj == null) throw new RequestException("触发限流策略或WS session没有开启");
        var retCode = obj.getRetcode();
        var status = obj.getStatus();
        if (retCode == -1 || status.equals("failed")) throw new RequestException("请求失败。");
        return obj;
    }


    public <T> T doRequest(Bot bot, ActionPath action, Map<String, Object> params, TypeReference<T> type) {
        var session = bot.getSession();
        JSONObject o = new JSONObject();
        o.putAll(params);
        var result = this.actionHandler.doActionRequest(session, action, o);
        if (result == null) throw new RequestException("触发限流策略或WS session没有开启");
        if (result.getString("status").equals("failed") || result.getInteger("retcode") == -1) {
            throw new RequestException("请求失败。");
        }
        //LOGGER.debug("请求成功。 内容: {}", result.toJSONString());
        return result.toJavaObject(type);
    }


}
