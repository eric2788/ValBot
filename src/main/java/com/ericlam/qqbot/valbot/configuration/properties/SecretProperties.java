package com.ericlam.qqbot.valbot.configuration.properties;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:secret.properties")
public class SecretProperties {
}
