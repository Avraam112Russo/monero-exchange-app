package com.n1nt3nd0.moneroexchangeapp.service.bot;

import com.n1nt3nd0.moneroexchangeapp.redis.RedisStorageTelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.MessageEntity;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static java.lang.Math.toIntExact;

@Service
@RequiredArgsConstructor
@Slf4j
public class BotCallBackQueryHandler {
    private final TelegramClient telegramClient;
    private final RedisStorageTelegramBot redisStorageTelegramBot;
    public void callBackQueryHandle(String call_data, long message_id, long chat_id, String username){
        // Set variables
        if (call_data.equals("Купить xmr")) {

            String answer = "Укажите сумму в BTC или же RUB:\n" +
                    "\n" +
                    "Пример: 0.001 или 0,001 или 5030 ";
            EditMessageText new_message = EditMessageText.builder()
                    .chatId(chat_id)
                    .messageId(toIntExact(message_id))
                    .text(answer)
                    .replyMarkup(InlineKeyboardMarkup
                            .builder()
                            .keyboardRow(
                                    new InlineKeyboardRow(InlineKeyboardButton
                                            .builder()
                                            .text("Отмена")
                                            .callbackData("Отмена")
                                            .build()
                                    )
                            )
                            .build())
                    .build();
            try {
                telegramClient.execute(new_message);

            } catch (TelegramApiException e) {
                log.error("error while update has callback query: " + e.getMessage());
                throw new RuntimeException();
            }
        } else if (call_data.equals("Сбербанк")) {

            String answer = "Скопируйте и отправьте боту свой кошелек XMR";
            EditMessageText new_message = EditMessageText.builder()
                    .chatId(chat_id)
                    .messageId(toIntExact(message_id))
                    .text(answer)
                    .replyMarkup(InlineKeyboardMarkup
                            .builder()
                            .keyboardRow(
                                    new InlineKeyboardRow(InlineKeyboardButton
                                            .builder()
                                            .text("Отмена")
                                            .callbackData("Отмена")
                                            .build()
                                    )
                            )
                            .build())
                    .build();
            try {
                telegramClient.execute(new_message);

            } catch (TelegramApiException e) {
                log.error("error while update has callback query: " + e.getMessage());
                throw new RuntimeException();
            }
        }
    }
}
