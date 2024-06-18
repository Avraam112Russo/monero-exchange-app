package com.n1nt3nd0.moneroexchangeapp.service.bot;

import com.n1nt3nd0.moneroexchangeapp.config.BotConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramBot implements LongPollingSingleThreadUpdateConsumer {
    private final BotConfig botConfig;

    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            String username = update.getMessage().getChat().getUserName();
            Long chatId = update.getMessage().getChatId();
            switch (text){
                case "/start" :
                    startCommand(chatId, text, username);
            }
        }
    }

    private void startCommand(Long chatId, String text, String username) {
       log.info("Message; "+text);
    }
}
