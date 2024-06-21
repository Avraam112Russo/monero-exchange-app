package com.n1nt3nd0.moneroexchangeapp.service.bot.botCommands;

import com.n1nt3nd0.moneroexchangeapp.dao.DaoBotState;
import com.n1nt3nd0.moneroexchangeapp.repository.TelegramBotUserRepository;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.Serializable;

public interface BotCommand extends Serializable {
    void execute(Update update,
                 TelegramClient telegramClient,
                 DaoBotState daoBotState,
                 TelegramBotUserRepository telegramBotUserRepository,
                 RestTemplate restTemplate
    );
}
