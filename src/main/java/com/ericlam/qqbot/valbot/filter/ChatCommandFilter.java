package com.ericlam.qqbot.valbot.filter;

import com.ericlam.qqbot.valbot.command.ChatCommand;
import com.ericlam.qqbot.valbot.command.GroupChatCommand;
import com.ericlam.qqbot.valbot.command.SpeakCommand;
import com.mikuac.shiro.core.Bot;
import com.mikuac.shiro.core.BotPlugin;
import com.mikuac.shiro.dto.event.message.GroupMessageEvent;
import org.jetbrains.annotations.NotNull;
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


@ChatCommand(
        name = "123",
        description = "123",
        subCommands = SpeakCommand.class
)
@Component
public class ChatCommandFilter extends BotPlugin {

    private static final String COMMAND_PREFIX = "!";

    @Resource(name = "commands")
    private List<Class<? extends GroupChatCommand>> mainCommands;

    @Autowired
    private BeanFactory factory;

    @Autowired
    private Logger logger;

    @Override
    public int onGroupMessage(@NotNull Bot bot, @NotNull GroupMessageEvent event) {
        if (!event.getMessage().startsWith(COMMAND_PREFIX)) return MESSAGE_IGNORE;
        if (mainCommands.isEmpty()){
            bot.sendGroupMsg(event.getGroupId(), "没有可用指令", true);
            return MESSAGE_BLOCK;
        }
        if (event.getSender().getRole().equals("member")){
            bot.sendGroupMsg(event.getGroupId(), "只有管理员才可使用指令", true);
            return MESSAGE_BLOCK;
        }
        String[] commands = event.getMessage().substring(1).split(" ");
        String cmd = commands[0];
        List<String> args = new ArrayList<>(Arrays.asList(Arrays.copyOfRange(commands, 1, commands.length)));
        List<ChatCommand> descriptors = new ArrayList<>();
        for (Class<? extends GroupChatCommand> cmdCls : mainCommands) {
            ChatCommand descriptor = getCommandDescriptor(cmdCls);
            descriptors.add(descriptor);
            if (labelMatch(descriptor, cmd)) {
                invokeCommand(bot, event, new ArrayList<>(), cmdCls, descriptor, args);
                return MESSAGE_BLOCK;
            }
        }
        logger.info("未知指令: !{}, 返回帮助讯息。", cmd);
        String help = descriptors.stream().map(c -> getHelpLine(List.of(), c)).collect(Collectors.joining("\n"));
        bot.sendGroupMsg(event.getGroupId(), help, true);
        return MESSAGE_BLOCK;
    }


    private void invokeCommand(
            Bot bot,
            GroupMessageEvent event,
            List<ChatCommand> parents,
            Class<? extends GroupChatCommand> command,
            ChatCommand descriptor,
            List<String> args
    ) {
        if (descriptor.subCommands().length > 0) {
            parents.add(descriptor);
            final String subCommand = args.size() > 0 ? args.get(0) : "";
            List<ChatCommand> subCommands = new ArrayList<>();
            for (Class<? extends GroupChatCommand> sub : descriptor.subCommands()) {
                ChatCommand subDescriptor = getCommandDescriptor(sub);
                subCommands.add(subDescriptor);
                if (labelMatch(subDescriptor, subCommand)) {
                    args.remove(0);
                    invokeCommand(bot, event, parents, sub, subDescriptor, args);
                    return;
                }
            }
            String help = subCommands.stream().map(c -> getHelpLine(parents, c)).collect(Collectors.joining("\n"));
            bot.sendGroupMsg(event.getGroupId(), help, true);
            return;
        }

        if (descriptor.placeholders().length > args.size()) {
            bot.sendGroupMsg(event.getGroupId(), getHelpLine(parents, descriptor), true);
            return;
        }

        GroupChatCommand chatCommand = factory.getBean(command);
        chatCommand.executeCommand(bot, event, args);
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


}
