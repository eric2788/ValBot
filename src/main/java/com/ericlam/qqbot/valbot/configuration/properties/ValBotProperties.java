package com.ericlam.qqbot.valbot.configuration.properties;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@PropertySource("classpath:secret.properties")
public class ValBotProperties {
}
