package com.n1nt3nd0.moneroexchangeapp.config;

import com.n1nt3nd0.moneroexchangeapp.service.bot.TelegramBotService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@Slf4j
@RequiredArgsConstructor
public class BotInitializer {
    private final TelegramBotService telegramBot;
    @PostConstruct
    public void init() {
        try {
            String botToken = "";
             TelegramBotsLongPollingApplication botsApplication =
                    new TelegramBotsLongPollingApplication();
            botsApplication.registerBot(botToken, telegramBot);
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }

    }

}
