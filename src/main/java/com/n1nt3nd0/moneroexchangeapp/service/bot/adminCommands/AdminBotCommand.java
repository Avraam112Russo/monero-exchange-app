package com.n1nt3nd0.moneroexchangeapp.service.bot.adminCommands;

import com.n1nt3nd0.moneroexchangeapp.dao.DaoBotState;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.Serializable;

public interface AdminBotCommand extends Serializable {
    void execute(Update update, DaoBotState daoBotState, TelegramClient telegramClient, String username);
}
