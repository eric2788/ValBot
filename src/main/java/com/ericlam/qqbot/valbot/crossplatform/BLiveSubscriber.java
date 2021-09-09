package com.ericlam.qqbot.valbot.crossplatform;

import com.ericlam.qqbot.valbot.dto.BLiveWebSocketData;
import com.ericlam.qqbot.valbot.dto.LiveRoomStatus;

import java.io.IOException;

public interface BLiveSubscriber {

    void subscribe(BLiveHandle handle, long room, BLiveWebSocketData ws) throws IOException;

    void doOnError(IOException e, long room);

    void subscribeLiveStatus(LiveRoomStatus status);

}
