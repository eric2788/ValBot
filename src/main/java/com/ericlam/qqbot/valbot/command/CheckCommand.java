package com.ericlam.qqbot.valbot.command;

import com.ericlam.qqbot.valbot.crossplatform.qq.QQGroupCommand;
import com.ericlam.qqbot.valbot.service.QQBotService;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.common.utils.ShiroUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.text.DateFormat;
import java.util.Date;
import java.util.List;

@Component
@ChatCommand(
        name = "check",
        description = "查成分",
        alias = "查成分",
        placeholders = "<用户>"
)
public class CheckCommand implements QQGroupCommand { // 不支援 discord 的指令

    @Autowired
    private DateFormat dateFormat;

    @Autowired
    private QQBotService qqBotService;

    @Override
    public void executeCommand(Bot bot, GroupMessageEvent event, List<String> args) {
        List<String> users = ShiroUtils.getAtList(event.getRawMessage());
        if (users.isEmpty()) {
            bot.sendGroupMsg(event.getGroupId(), "找不到用户", true);
        }
        var member = qqBotService.validateNotError(bot.getGroupMemberInfo(event.getGroupId(), event.getUserId(), false)).getData();
        StringBuilder builder = new StringBuilder();
        builder.append("QQ-ID: ").append(member.getUserId()).append("\n");
        builder.append("名称: ").append(member.getNickname()).append("\n");
        builder.append("群昵称: ").append(member.getCard()).append("\n");
        builder.append("等级: ").append(member.getLevel()).append("\n");
        builder.append("地区: ").append(member.getArea()).append("\n");
        builder.append("年龄: ").append(member.getAge()).append("\n");
        builder.append("加群时间: ").append(toLocalDateTime(member.getJoinTime())).append("\n");
        builder.append("上次发言时间: ").append(toLocalDateTime(member.getLastSentTime())).append("\n");
        builder.append("性别: ").append(member.getSex()).append("\n");
        builder.append("专属头衔: ").append(member.getTitle()).append("\n");
        builder.append("是否不良记录成员: ").append(member.isUnfriendly()).append("\n");
        bot.sendGroupMsg(event.getGroupId(), MsgUtils
                .builder()
                .text(builder.toString())
                .reply(event.getMessageId())
                .build(), false);
    }

    private String toLocalDateTime(long time) {
        Date date = new Date(time * 1000L);
        return dateFormat.format(date);
    }
}
