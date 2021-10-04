package com.ericlam.qqbot.valbot.crossplatform.subscriber;

import com.ericlam.qqbot.valbot.crossplatform.livehandle.TweetsHandle;
import com.ericlam.qqbot.valbot.dto.TweetStreamData;

import java.io.IOException;

public interface TwitterSubscriber extends LiveSubscriber{

    void subscribe(TweetsHandle handle, String username, TweetStreamData data) throws IOException;

}
