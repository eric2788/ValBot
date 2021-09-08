package com.ericlam.qqbot.valbot.redis.wshandle;

import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
import com.mikuac.shiro.core.Bot;

import java.io.IOException;

public interface BLiveHandle {

    void handle(Bot bot, long room, BLiveWebSocketData ws) throws IOException;

}
