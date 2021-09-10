package com.ericlam.qqbot.valbot.crossplatform.qq;

import com.ericlam.qqbot.valbot.crossplatform.BLiveHandle;
import com.ericlam.qqbot.valbot.crossplatform.BLiveSubscriber;
import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
import com.ericlam.qqbot.valbot.dto.LiveRoomStatus;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.BotContainer;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.util.Map;

@Component
public class QQBLiveSubscriber implements BLiveSubscriber {


    @Value("${val.group}")
    private long groupId;

    @Value("${val.bot}")
    private long botId;

    @Autowired
    private BotContainer container;

    @Autowired
    private Logger logger;

    @Resource(name = "translate-status")
    private Map<String, String> translation;

    @Override
    public void subscribe(BLiveHandle handle, long room, BLiveWebSocketData ws) throws IOException {
        if (!(handle instanceof QQBLiveHandle qqbLiveHandle)) return; // 非QQ广播处理
        var bot = container.robots.get(botId);
        if (bot == null){
            logger.warn("QQ机器人未上线({})，已略过。", botId);
            return;
        }
        qqbLiveHandle.handle(bot, groupId, room, ws);
    }

    @Override
    public void doOnError(IOException e, long room) {
        var bot = container.robots.get(botId);
        if (bot == null){
            logger.warn("QQ机器人未上线({})，已略过。", botId);
            return;
        }
        bot.sendGroupMsg(groupId, "解析WS时出现错误: "+e.getMessage(), true);
    }

    @Override
    public void subscribeLiveStatus(LiveRoomStatus status) {
        var bot = container.robots.get(botId);
        if (bot == null){
            logger.warn("QQ机器人未上线({})，已略过。", botId);
            return;
        }
        String room = status.id == -1 ? "监控服务器" : "房间 " + status.id;
        if (status.status.startsWith("error:")){
            String errorMsg = status.status.split(":")[1];
            String msg = MsgUtils.builder().text(room).text(" ").text("初始化监听时出现错误: ").text(errorMsg).build();
            bot.sendGroupMsg(groupId, msg, true);
            return;
        }
        String statusTxt = translation.getOrDefault(status.status, status.status);
        String msg = MsgUtils.builder().text(room).text(" ").text(statusTxt).text("。").build();
        bot.sendGroupMsg(groupId, msg, true);
    }


}
