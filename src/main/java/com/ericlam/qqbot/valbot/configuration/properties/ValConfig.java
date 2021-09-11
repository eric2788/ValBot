package com.ericlam.qqbot.valbot.configuration.properties;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;

@Getter
@Setter
@ConfigurationProperties(prefix = "val")
@PropertySource("classpath:secret.properties")
public class ValConfig {

    private long groupId;
    private long botId;

}
