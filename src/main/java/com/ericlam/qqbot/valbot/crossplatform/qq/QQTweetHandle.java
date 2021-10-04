package com.ericlam.qqbot.valbot.crossplatform.qq;

import com.ericlam.qqbot.valbot.crossplatform.livehandle.TweetsHandle;
import com.ericlam.qqbot.valbot.dto.TweetStreamData;
import com.mikuac.shiro.core.Bot;

import java.io.IOException;

public interface QQTweetHandle extends TweetsHandle {

    void handle(Bot bot, long groupId, String username, TweetStreamData data) throws IOException;

}
