package com.ericlam.qqbot.valbot.crossplatform.subscriber;

import com.ericlam.qqbot.valbot.dto.LiveRoomStatus;

import java.io.IOException;

public interface LiveSubscriber {

    void doOnError(IOException e, String room);

    void subscribeLiveStatus(LiveRoomStatus status);

}
