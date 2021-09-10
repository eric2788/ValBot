package com.ericlam.qqbot.valbot.configuration;

import com.ericlam.qqbot.valbot.configuration.properties.DiscordConfig;
import com.ericlam.qqbot.valbot.crossplatform.discord.DiscordMessageEventSource;
import com.ericlam.qqbot.valbot.manager.ChatCommandManager;
import com.ericlam.qqbot.valbot.manager.ChatResponseManager;
import discord4j.common.util.Snowflake;
import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import discord4j.core.event.domain.lifecycle.DisconnectEvent;
import discord4j.core.event.domain.lifecycle.ReadyEvent;
import discord4j.core.event.domain.lifecycle.ReconnectEvent;
import discord4j.core.event.domain.message.MessageCreateEvent;
import discord4j.core.object.entity.Entity;
import discord4j.core.object.entity.Message;
import discord4j.core.object.entity.User;
import discord4j.core.object.entity.channel.GuildMessageChannel;
import discord4j.core.object.entity.channel.TextChannel;
import discord4j.rest.util.Color;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Objects;
import java.util.Random;


@Configuration
public class DiscordAppConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscordAppConfig.class);

    @Bean
    public GatewayDiscordClient discordClient(
            DiscordConfig discordSettings,
            List<MessageCreateHandle> handleList
    ) {
        LOGGER.debug("Launching Discord Bot with {}", discordSettings.toString());
        var client = DiscordClientBuilder.create(discordSettings.getToken())
                .build()
                .login()
                .block();

        Objects.requireNonNull(client);

        LOGGER.info("正在获取广播频道 {}...", discordSettings.getNewsChannel());

        client.getChannelById(Snowflake.of(discordSettings.getLogChannel())).ofType(TextChannel.class).blockOptional().ifPresentOrElse(channel -> {
            client.getEventDispatcher().on(ReadyEvent.class)
                    .filter(e -> e.getSelf().equals(client.getSelf().block()))
                    .flatMap(e -> channel.createMessage("本机器人已成功启动。")).subscribe();

            client.getEventDispatcher().on(DisconnectEvent.class)
                    .filter(e -> e.getClient() == client)
                    .flatMap(e -> channel.createMessage("机器人失去连线，原因: " + e.getStatus().getReason()
                            .orElseGet(() -> e.getCause()
                                    .map(Throwable::getMessage)
                                    .orElse("UNKNOWN")
                            ))).subscribe();

            client.getEventDispatcher().on(ReconnectEvent.class)
                    .filter(e -> e.getClient() == client)
                    .flatMap(e -> channel.createMessage("机器人已重新上线。"))
                    .subscribe();
        }, () -> LOGGER.warn("输出日志频道为 null ， 无法发送通知。"));


        client.getEventDispatcher().on(MessageCreateEvent.class)
                .filter(e ->
                        e.getGuildId().map(s -> s.asLong() == discordSettings.getGuild()).orElse(false) // 非瓦群无视
                                && e.getMessage().getAuthor().map(u -> !u.isBot()).orElse(false) // 机器人无视
                )
                .subscribe(event -> {
                    var guildMessageChannel = event.getMessage().getChannel().ofType(GuildMessageChannel.class).blockOptional();
                    if (guildMessageChannel.isEmpty()){
                        LOGGER.debug("不是 GuildMessageChannel, 已略过。");
                        return;
                    }
                    DiscordMessageEventSource source = new DiscordMessageEventSource(guildMessageChannel.get(), event);
                    for (MessageCreateHandle handle : handleList) {
                        LOGGER.debug("正在处理: {}", handle.getClass().getSimpleName());
                        if (!handle.handle(source)) {
                            break;
                        }
                    }
                });
        return client;
    }


    @Bean("discord-bot-id")
    public Snowflake botId(GatewayDiscordClient client){
        return client.getSelf().map(User::getId).block();
    }


    @Bean
    public List<MessageCreateHandle> handleList(BeanFactory factory) {
        return List.of(
                factory.getBean(CommandMessageCreateHandle.class),
                factory.getBean(ChatMessageCreateHandle.class)
        );
    }

    // message create handlers

    @FunctionalInterface
    public interface MessageCreateHandle {

        boolean handle(DiscordMessageEventSource eventSource);

    }

    @Order(1)
    @Component
    public static class CommandMessageCreateHandle implements MessageCreateHandle {

        @Autowired
        private com.ericlam.qqbot.valbot.configuration.properties.DiscordConfig discordSetting;

        @Autowired
        private ChatCommandManager chatCommandManager;

        @Override
        public boolean handle(DiscordMessageEventSource eventSource) {
            var event = eventSource.event();
            if (chatCommandManager.isNotCommand(event.getMessage().getContent())) { // 如为无效指令
                return true; // 则无视
            }
            if (event.getMessage()
                    .getChannel()
                    .map(Entity::getId)
                    .map(id -> id.asLong() != discordSetting.getTextChannel())
                    .blockOptional()
                    .orElse(true)) { // 如果输入指令的频道为非机器人频道
                return true; // 则无视
            }
            Message message = event.getMessage();
            var isAdmin = event.getMember().map(m -> m.getHighestRole().block()).map(role -> role.getId().asLong() == discordSetting.getAdminRole()).orElse(false);

            // command
            var result = chatCommandManager.onReceiveMessage(
                    message.getContent(),
                    isAdmin,
                    eventSource
            );

            LOGGER.debug("指令 !{} 返回结果为 {}", message.getContent(), result);

            switch (result) {
                case ChatCommandManager.CommandResult.PASS:
                case ChatCommandManager.CommandResult.NON_PASS:
                    break;
                case ChatCommandManager.CommandResult.UNAVAILABLE:
                    message.getChannel().flatMap(c -> c.createMessage(spec ->
                            spec.setContent("此指令没有支援的平台...").setMessageReference(event.getMessage().getId())
                    )).subscribe();
                    break;
                default:
                    message.getChannel().flatMap(c -> c.createMessage(spec -> {
                        spec.setMessageReference(event.getMessage().getId());
                        if (result.startsWith("help:")){
                            var helpMsg = result.replace("help:", "");
                            spec.addEmbed(em -> {
                                em.addField("指令列表", helpMsg, false);
                            });
                        }else{
                            spec.setContent(result);
                        }
                    })).subscribe();
                    break;
            }

            return result.equals(ChatCommandManager.CommandResult.NON_PASS);
        }
    }

    private static final Random RANDOM = new Random();

    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean("random")
    public Color randomColor(){
        return Color.of(RANDOM.nextInt(16777215));
    }

    @Order(2)
    @Component
    public static record ChatMessageCreateHandle(ChatResponseManager responseManager) implements MessageCreateHandle {

        @Override
        public boolean handle(DiscordMessageEventSource eventSource) {
            return responseManager.onReceiveMessage(eventSource);
        }

    }

}
