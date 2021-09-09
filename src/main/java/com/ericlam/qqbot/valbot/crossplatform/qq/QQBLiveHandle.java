package com.ericlam.qqbot.valbot.crossplatform.qq;

import com.ericlam.qqbot.valbot.crossplatform.BLiveHandle;
import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
import com.mikuac.shiro.core.Bot;

import java.io.IOException;

public interface QQBLiveHandle extends BLiveHandle {

    void handle(Bot bot, long groupId, long room, BLiveWebSocketData ws) throws IOException;

}
