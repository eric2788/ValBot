package com.ericlam.qqbot.valbot.manager;

import com.ericlam.qqbot.valbot.RequestException;
import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.crossplatform.GroupCommand;
import com.ericlam.qqbot.valbot.crossplatform.MessageEventSource;
import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordGroupCommand;
import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordMessageEventSource;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQGroupCommand;
import com.ericlam.qqbot.valbot.crossplatform.qq.QQMessageEventSource;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.entity.channel.MessageChannel;
import org.slf4j.Logger;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ChatCommandManager {


    private static final String COMMAND_PREFIX = "!";

    @Resource(name = "commands")
    private List<Class<? extends GroupCommand>> mainCommands;

    @Autowired
    private BeanFactory factory;

    @Autowired
    private Logger logger;

    public boolean isNotCommand(String content) {
        return !content.startsWith(COMMAND_PREFIX);
    }

    public String onReceiveMessage(String content, boolean isAdmin, MessageEventSource source) {
        if (isNotCommand(content)) return CommandResult.NON_PASS;
        if (mainCommands.isEmpty()) {
            return "没有可用指令";
        }
        if (!isAdmin) {
            return "只有管理员才可使用指令";
        }
        String[] commands = content.substring(1).split(" ");
        String cmd = commands[0];
        List<String> args = new ArrayList<>(Arrays.asList(Arrays.copyOfRange(commands, 1, commands.length)));
        List<ChatCommand> descriptors = new ArrayList<>();
        for (Class<? extends GroupCommand> cmdCls : mainCommands) {
            ChatCommand descriptor = getCommandDescriptor(cmdCls);
            descriptors.add(descriptor);
            if (labelMatch(descriptor, cmd)) {
                return invokeCommand(source, new ArrayList<>(), cmdCls, descriptor, args);
            }
        }
        logger.info("未知指令: !{}, 返回帮助讯息。", cmd);
        return "help:"+descriptors.stream().map(c -> getHelpLine(List.of(), c)).collect(Collectors.joining("\n"));
    }


    private String invokeCommand(
            MessageEventSource source,
            List<ChatCommand> parents,
            Class<? extends GroupCommand> command,
            ChatCommand descriptor,
            List<String> args
    ) {
        if (descriptor.subCommands().length > 0) {
            parents.add(descriptor);
            final String subCommand = args.size() > 0 ? args.get(0) : "";
            List<ChatCommand> subCommands = new ArrayList<>();
            for (Class<? extends GroupCommand> sub : descriptor.subCommands()) {
                ChatCommand subDescriptor = getCommandDescriptor(sub);
                subCommands.add(subDescriptor);
                if (labelMatch(subDescriptor, subCommand)) {
                    args.remove(0);
                    return invokeCommand(source, parents, sub, subDescriptor, args);
                }
            }
            return "help:"+subCommands.stream().map(c -> getHelpLine(parents, c)).collect(Collectors.joining("\n"));
        }

        if (Arrays.stream(descriptor.placeholders()).filter(s -> !s.startsWith("[") || !s.endsWith("]")).count() > args.size()) {
            return getHelpLine(parents, descriptor);
        }

        GroupCommand chatCommand = factory.getBean(command);

        // if more than two platforms, use data structure
        // 若平台超过两个，则使用 data structure
        if (source instanceof QQMessageEventSource qqMessageEventSource) {
            GroupMessageEvent event = qqMessageEventSource.messageEvent();
            Bot qqBot = qqMessageEventSource.qqBot();
            if (!(chatCommand instanceof QQGroupCommand qqGroupCommand)) {
                logger.info("來源於 QQ 但 不支援作为QQ指令，已略過。");
                return "此指令不支援 QQ 平台。";
            } else {
                try {
                    qqGroupCommand.executeCommand(qqBot, event, args);
                }catch (RequestException e){
                    logger.warn("处理指令时出现错误: {}", e.getMessage());
                    qqBot.sendGroupMsg(event.getGroupId(), "处理指令时出现错误: "+e.getMessage(), true);
                }catch (Exception e){
                    logger.error("处理指令时出现错误", e);
                    qqBot.sendGroupMsg(event.getGroupId(), "处理指令时出现错误: "+e.getClass().getSimpleName(), true);
                }
            }
        } else if (source instanceof DiscordMessageEventSource discordMessageEventSource) {
            GuildMessageChannel channel = discordMessageEventSource.channel();
            MessageCreateEvent event = discordMessageEventSource.event();
            if (!(chatCommand instanceof DiscordGroupCommand discordGroupCommand)) {
                logger.info("來源於 Discord 但 不支援作为Discord指令，已略過。");
                return "此指令不支援 Discord 平台。";
            } else {
                try {
                    discordGroupCommand.executeCommand(channel, event, args);
                }catch (RequestException e){
                    logger.warn("处理指令时出现错误: {}", e.getMessage());
                    channel.createMessage("处理指令时出现错误: "+e.getMessage()).subscribe();
                }catch (Exception e){
                    logger.error("处理指令时出现错误", e);
                    channel.createMessage("处理指令时出现错误: "+e.getClass().getSimpleName()).subscribe();
                }
            }
        } else {
            return CommandResult.UNAVAILABLE;
        }

        return CommandResult.PASS;
    }


    private String getHelpLine(List<ChatCommand> parents, ChatCommand chatCommand) {
        StringBuilder builder = new StringBuilder("!");
        for (ChatCommand parent : parents) {
            builder.append(parent.name()).append(" ");
        }
        builder.append(chatCommand.name()).append(" ");
        for (String placeholder : chatCommand.placeholders()) {
            builder.append(placeholder).append(" ");
        }
        builder.append(" - ").append(chatCommand.description());
        return builder.toString();
    }

    private boolean labelMatch(ChatCommand command, String arg) {
        List<String> labels = new ArrayList<>(Arrays.asList(command.alias()));
        labels.add(command.name());
        return labels.stream().anyMatch(l -> l.equalsIgnoreCase(arg));
    }


    private ChatCommand getCommandDescriptor(Class<?> cmd) {
        return Optional.ofNullable(cmd.getAnnotation(ChatCommand.class)).orElseThrow(() -> new IllegalStateException("command class " + cmd + " do not have @ChatCommand annotation"));
    }

    public static class CommandResult {

        public static final String PASS = "PASS";

        public static final String NON_PASS = "NON_PASS";

        public static final String UNAVAILABLE = "UNAVAILABLE";

    }

}
