package com.ericlam.qqbot.valbot.dto.res;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class EssenceInfo {

    @JSONField(name = "sender_id")
    private long senderId;

    @JSONField(name = "sender_nick")
    private String senderNick;

    @JSONField(name = "sender_time")
    private long senderTime;

    @JSONField(name = "operator_id")
    private long operatorId;

    @JSONField(name = "operator_nick")
    private String operatorNick;

    @JSONField(name = "operator_time")
    private long operatorTime;

    @JSONField(name = "message_id")
    private int messageId;

}
