package com.ericlam.qqbot.valbot.dto.res;


import com.alibaba.fastjson.annotation.JSONField;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Message {

    @JSONField(name = "message_id")
    private int messageId;

    @JSONField(name = "real_id")
    private int realId;

    @JSONField(name = "sender")
    private GroupMessageEvent.GroupSender sender;

    @JSONField(name = "time")
    private int time;

    @JSONField(name = "message")
    private String message;

    @JSONField(name = "raw_message")
    private String rawMessage;

}
