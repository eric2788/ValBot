package com.ericlam.qqbot.valbot;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("classpath:secret.properties")
public class SecretProperties {

    public Val val;

    public Discord discord;


    public static class Discord {


        public String token;

        public String textChannel;

        public String newsChannel;

    }


    public static class Val {

        public long group;

        public long bot;
    }
}
