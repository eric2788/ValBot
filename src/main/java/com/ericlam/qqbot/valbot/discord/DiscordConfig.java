package com.ericlam.qqbot.valbot.discord;

import discord4j.core.DiscordClientBuilder;
import discord4j.core.GatewayDiscordClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiscordConfig {

    @Bean
    public GatewayDiscordClient discordClient(@Value("${discord.token}") String token) {
        return DiscordClientBuilder.create(token)
                .build()
                .login()
                .block();
    }

}
