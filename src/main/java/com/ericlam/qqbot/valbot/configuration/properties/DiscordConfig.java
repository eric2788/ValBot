package com.ericlam.qqbot.valbot.configuration.properties;


import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Getter
@Setter
@ConfigurationProperties(prefix = "discord")
public class DiscordConfig {

    private String token;

    private long guild;

    private long textChannel;

    private long newsChannel;

    private long logChannel;

    private long adminRole;


    @Override
    public String toString() {
        return "DiscordConfig{" +
                "guild=" + guild +
                ", textChannel=" + textChannel +
                ", newsChannel=" + newsChannel +
                ", logChannel=" + logChannel +
                ", adminRole=" + adminRole +
                '}';
    }
}
