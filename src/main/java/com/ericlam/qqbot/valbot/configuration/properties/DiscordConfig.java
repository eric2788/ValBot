package com.ericlam.qqbot.valbot.configuration.properties;


import org.springframework.boot.context.properties.ConfigurationProperties;


@ConfigurationProperties(prefix = "discord")
public class DiscordConfig {

    private String token;

    private long guild;

    private long textChannel;

    private long newsChannel;

    private long logChannel;

    private long adminRole;

    public long getLogChannel() {
        return logChannel;
    }

    public void setLogChannel(long logChannel) {
        this.logChannel = logChannel;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public long getGuild() {
        return guild;
    }

    public void setGuild(long guild) {
        this.guild = guild;
    }

    public long getTextChannel() {
        return textChannel;
    }

    public void setTextChannel(long textChannel) {
        this.textChannel = textChannel;
    }

    public long getNewsChannel() {
        return newsChannel;
    }

    public void setNewsChannel(long newsChannel) {
        this.newsChannel = newsChannel;
    }

    public long getAdminRole() {
        return adminRole;
    }

    public void setAdminRole(long adminRole) {
        this.adminRole = adminRole;
    }


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
