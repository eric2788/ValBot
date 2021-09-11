package com.ericlam.qqbot.valbot.configuration.properties;

import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;


@Setter
@ConfigurationProperties(prefix = "spring.redis")
public class RedisConfig {

    private String host;
    private int port;
    private int database;
    private String password;

    @Override
    public String toString() {
        return "RedisConfig{" +
                "host='" + host + '\'' +
                ", port=" + port +
                ", database=" + database +
                '}';
    }
}
