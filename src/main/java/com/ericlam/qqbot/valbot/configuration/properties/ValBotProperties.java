package com.ericlam.qqbot.valbot.configuration.properties;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;


@Configuration
@PropertySource(value = {"classpath:/secret.properties", "file:/config/secret.properties"}, ignoreResourceNotFound = true)
public class ValBotProperties {
}
