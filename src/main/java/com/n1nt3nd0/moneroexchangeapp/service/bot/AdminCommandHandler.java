package com.n1nt3nd0.moneroexchangeapp.service.bot;

import com.n1nt3nd0.moneroexchangeapp.dao.DaoBotState;
import com.n1nt3nd0.moneroexchangeapp.repository.TelegramBotUserRepository;
import com.n1nt3nd0.moneroexchangeapp.service.bot.botCommands.BotCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
@Slf4j
public class AdminCommandHandler {

    public void handleAdminCommand(Update update) {

    }
}
