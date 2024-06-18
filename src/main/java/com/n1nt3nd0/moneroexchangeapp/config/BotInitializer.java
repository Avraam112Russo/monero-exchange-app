package com.n1nt3nd0.moneroexchangeapp.config;

import com.n1nt3nd0.moneroexchangeapp.service.bot.TelegramBot;
import jakarta.annotation.PostConstruct;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
@RequiredArgsConstructor
public class BotInitializer {
    private final TelegramBot telegramBot;
    @PostConstruct
    public void init() {
        try {
            String botToken = "7438604237:AAH-rHbOYV5xQ1eamxwSKa9XdUuRM_hysAs";
             TelegramBotsLongPollingApplication botsApplication =
                    new TelegramBotsLongPollingApplication();
            botsApplication.registerBot(botToken, telegramBot);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }

    }

}
