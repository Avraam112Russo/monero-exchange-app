package com.n1nt3nd0.moneroexchangeapp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.client.okhttp.OkHttpTelegramClient;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Configuration
public class BotConfig {
    @Bean
    public TelegramClient telegramClient(){
        return new OkHttpTelegramClient("7438604237:AAH-rHbOYV5xQ1eamxwSKa9XdUuRM_hysAs");
    }
}
