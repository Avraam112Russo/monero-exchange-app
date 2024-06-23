package com.n1nt3nd0.moneroexchangeapp.service.bot;

import com.n1nt3nd0.moneroexchangeapp.dao.DaoBotState;
import com.n1nt3nd0.moneroexchangeapp.repository.TelegramBotUserRepository;
import com.n1nt3nd0.moneroexchangeapp.service.bot.adminCommands.AdminBotCommand;
import com.n1nt3nd0.moneroexchangeapp.service.bot.botCommands.BotCommand;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

@Service
@Slf4j
@RequiredArgsConstructor
public class AdminCommandHandler {

    private final DaoBotState daoBotState;
    private final TelegramClient telegramClient;
    public void handleAdminCommand(Update update) {
        String text = update.getMessage().getText();
        String[] result = text.split("#");
        String commandName = result[0]; // /confirm#someusername  /complete#someusername
        String username = result[1];
        AdminBotCommand confirmPaymentCommand = daoBotState.getAdminCommands(commandName);
        confirmPaymentCommand.execute(update, daoBotState, telegramClient, username);
    }
}
