package com.ericlam.qqbot.valbot.filter;

import com.ericlam.qqbot.valbot.crossplatform.qq.QQMessageEventSource;
import com.ericlam.qqbot.valbot.manager.ChatCommandManager;
import com.mikuac.shiro.common.utils.MsgUtils;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;


@Component
public class ChatCommandFilter extends BotPlugin {


    @Autowired
    private ChatCommandManager chatCommandManager;

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
       String result = chatCommandManager.onReceiveMessage(
               event.getMessage(),
               !event.getSender().getRole().equals("member"),
               new QQMessageEventSource(event, bot));
       if (result.equals(ChatCommandManager.CommandResult.NON_PASS)){
           return MESSAGE_IGNORE;
       }else{

           switch (result){
               case ChatCommandManager.CommandResult.PASS:
                   break;
               case ChatCommandManager.CommandResult.UNAVAILABLE:
                   bot.sendGroupMsg(event.getGroupId(), "此指令没有支援的平台...", true);
                   break;
               default:
                   if (result.startsWith("help:")){ // 返回幫助訊息
                        // do something ??
                        result = result.replace("help:", "");
                   }
                   /* 發群會被風控，改成私訊？
                   bot.sendGroupMsg(event.getGroupId(),
                           MsgUtils.builder()
                                   .reply(event.getMessageId())
                                   .text("指令列表").text("\n")
                                   .text(result)
                                   .build(), false);

                    */
                   bot.sendPrivateMsg(event.getUserId(),
                            MsgUtils.builder()
                                    .text("指令列表").text("\n")
                                    .text(result)
                                    .build(), false);
                   break;
           }

           return MESSAGE_BLOCK;
       }
    }



}
