package com.n1nt3nd0.moneroexchangeapp.service.bot.adminCommands;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface AdminBotCommand {
    void execute(Update update);
}
