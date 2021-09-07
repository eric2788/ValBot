package com.ericlam.qqbot.valbot.redis;

import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
import com.ericlam.qqbot.valbot.dto.SuperChatMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.data.redis.connection.Message;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
@Component
public class BilibiliLiveSubscriber extends BotMessageListener {

    @Autowired
    private ObjectMapper mapper;

    @Value("${val.group}")
    private long groupId;

    @Autowired
    private Logger logger;

    @Override
    public void onRedisMessage(Bot bot, @NotNull Message message, byte[] bytes) {
        String roomIdStr = new String(message.getChannel()).replace("blive:", "");
        long room;
        try {
            room = Long.parseLong(roomIdStr);
        } catch (NumberFormatException e) {
            logger.warn("無法解析頻道 room id");
            return;
        }
        try {
            var info = mapper.readValue(message.getBody(), BLiveWebSocketData.class);
            // TODO: use interface instead of if else hell
            logger.info("(房间{}) 收到WS指令: {}", room, info);
            if (info.command.equals(BLiveWebSocketData.LIVE)) {
                var builder = MsgUtils.builder()
                        .text("開播通知:").text("\n")
                        .text("標題: ").text(info.data.title).text("\n")
                        .text("用戶: ").text(info.data.name).text("(" + info.data.uid + ")").text("\n")
                        .text("連結: ").text("https://live.bilibili.com/").text(String.valueOf(room));
                if (info.data.cover != null) {
                    builder.img(info.data.cover);
                }
                bot.sendGroupMsg(groupId, builder.build(), false);
            }else if (info.command.equals(BLiveWebSocketData.SUPER_CHAT_MESSAGE)){
                var sc = mapper.readValue(info.data.content.getBytes("data"), SuperChatMessage.class);
                MsgUtils.builder().text("从房间 ").text(info.data.name).text(" 收到 SC:");
            }else if (info.command.equals(BLiveWebSocketData.DANMU_MSG)){
                var data = info.data.content.getJSONArray("info");
                var danmaku = data.getString(1);
                var uname = data.getJSONArray(2).getString(1);
                String msg = MsgUtils.builder().text("从").text(info.data.name).text("的直播房间收到弹幕讯息[").text(uname).text(": ").text(danmaku).text("]").build();
                bot.sendGroupMsg(groupId, msg, true);
            }
        } catch (IOException e) {
            bot.sendGroupMsg(groupId, "解析WS數據時出現錯誤: " + e.getMessage(), true);
            logger.warn("Error while parsing data ", e);
        }
    }
}
