package com.ericlam.qqbot.valbot.crossplatform.qq;

import com.ericlam.qqbot.valbot.crossplatform.livehandle.YTLiveHandle;
import com.ericlam.qqbot.valbot.dto.YoutubeLiveInfo;
import com.mikuac.shiro.core.Bot;

import java.io.IOException;

public interface QQYTLiveHandle extends YTLiveHandle {

    void handle(Bot bot, long groupId, String channelId, YoutubeLiveInfo info) throws IOException;

}
