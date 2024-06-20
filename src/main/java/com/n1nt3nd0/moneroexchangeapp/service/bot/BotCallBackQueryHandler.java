package com.n1nt3nd0.moneroexchangeapp.service.bot;

import com.n1nt3nd0.moneroexchangeapp.dao.DaoBotState;
import com.n1nt3nd0.moneroexchangeapp.model.BotLastState;
import com.n1nt3nd0.moneroexchangeapp.model.bot_last_state.BotStateEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.UUID;

import static java.lang.Math.toIntExact;

@Service
@RequiredArgsConstructor
@Slf4j
public class BotCallBackQueryHandler {
    private final TelegramClient telegramClient;
    private final DaoBotState daoBotState;
    public void callBackQueryHandle(String call_data, long message_id, long chat_id, String username){


        switch (call_data) {
            case "Купить xmr" :
                buyMoneroCommand(chat_id, username, message_id );
        }
//
//        // Set variables
//        if (call_data.equals("Купить xmr")) {
//
//            String answer = "Укажите сумму в BTC или же RUB:\n" +
//                    "\n" +
//                    "Пример: 0.001 или 0,001 или 5030 ";
//            EditMessageText new_message = EditMessageText.builder()
//                    .chatId(chat_id)
//                    .messageId(toIntExact(message_id))
//                    .text(answer)
//                    .replyMarkup(InlineKeyboardMarkup
//                            .builder()
//                            .keyboardRow(
//                                    new InlineKeyboardRow(InlineKeyboardButton
//                                            .builder()
//                                            .text("Отмена")
//                                            .callbackData("Отмена")
//                                            .build()
//                                    )
//                            )
//                            .build())
//                    .build();
//            try {
//                telegramClient.execute(new_message);
//
//            } catch (TelegramApiException e) {
//                log.error("error while update has callback query: " + e.getMessage());
//                throw new RuntimeException();
//            }
//        } else if (call_data.equals("Сбербанк")) {
//
//            String answer = "Скопируйте и отправьте боту свой кошелек XMR";
//            EditMessageText new_message = EditMessageText.builder()
//                    .chatId(chat_id)
//                    .messageId(toIntExact(message_id))
//                    .text(answer)
//                    .replyMarkup(InlineKeyboardMarkup
//                            .builder()
//                            .keyboardRow(
//                                    new InlineKeyboardRow(InlineKeyboardButton
//                                            .builder()
//                                            .text("Отмена")
//                                            .callbackData("Отмена")
//                                            .build()
//                                    )
//                            )
//                            .build())
//                    .build();
//            try {
//                telegramClient.execute(new_message);
//
//            } catch (TelegramApiException e) {
//                log.error("error while update has callback query: " + e.getMessage());
//                throw new RuntimeException();
//            }
//        }
    }

    private void buyMoneroCommand(long chatId, String username, long messageID) {

        String answer = "Укажите сумму в BTC или же RUB:\n" +
                "\n" +
                "Пример: 0.001 или 0,001 или 5030 ";
        EditMessageText new_message = EditMessageText.builder()
                .chatId(chatId)
                .messageId(toIntExact(messageID))
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
            daoBotState.updateCurrentlyBotState(
                    BotLastState.builder()
                            .chatId(String.valueOf(chatId))
                            .lastBotStateEnum(BotStateEnum.BUY_XMR_COMMAND)
                            .id(UUID.randomUUID().toString())
                            .build()
            );;
        } catch (TelegramApiException e) {
            log.error("error while update has callback query: " + e.getMessage());
            throw new RuntimeException();
        }
    }
}
