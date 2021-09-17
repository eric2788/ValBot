package com.ericlam.qqbot.valbot.crossplatform.qq;

import com.ericlam.qqbot.valbot.crossplatform.livehandle.BiliLiveHandle;
import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
import com.mikuac.shiro.core.Bot;

import java.io.IOException;

public interface QQBiliLiveHandle extends BiliLiveHandle {

    void handle(Bot bot, long groupId, long room, BLiveWebSocketData ws) throws IOException;

}
