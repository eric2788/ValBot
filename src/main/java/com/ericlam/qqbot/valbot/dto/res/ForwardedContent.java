package com.ericlam.qqbot.valbot.dto.res;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ForwardedContent {

    @JSONField(name = "messages")
    private List<ForwardMessage> messages;

    @Getter
    @Setter
    public static class ForwardMessage {

        @JSONField(name = "content")
        private String content;

        @JSONField(name = "sender")
        private Sender sender;

        @JSONField(name = "time")
        private long time;


        @Setter
        @Getter
        public static class Sender {

            @JSONField(name = "nickname")
            private String nickName;
            @JSONField(name = "user_id")
            private int userId;
        }
    }

}
