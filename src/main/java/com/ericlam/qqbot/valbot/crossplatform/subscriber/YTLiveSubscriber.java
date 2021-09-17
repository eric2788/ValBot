package com.ericlam.qqbot.valbot.crossplatform.subscriber;

import com.ericlam.qqbot.valbot.crossplatform.livehandle.YTLiveHandle;
import com.ericlam.qqbot.valbot.dto.YoutubeLiveInfo;

import java.io.IOException;

public interface YTLiveSubscriber {

    void subscribe(YTLiveHandle handle, String channelId, YoutubeLiveInfo info) throws IOException;


}
