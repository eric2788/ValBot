package com.ericlam.qqbot.valbot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@ConfigurationPropertiesScan("com.ericlam.qqbot.valbot.configuration.properties")
@SpringBootApplication
public class ValBotApplication {

    public static void main(String[] args) {
        SpringApplication.run(ValBotApplication.class, args);
    }

}
