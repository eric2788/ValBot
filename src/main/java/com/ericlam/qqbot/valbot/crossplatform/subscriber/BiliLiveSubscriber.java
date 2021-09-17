package com.ericlam.qqbot.valbot.crossplatform.subscriber;

import com.ericlam.qqbot.valbot.crossplatform.livehandle.BiliLiveHandle;
import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;

import java.io.IOException;

public interface BiliLiveSubscriber {

    void subscribe(BiliLiveHandle handle, long room, BLiveWebSocketData ws) throws IOException;

}
