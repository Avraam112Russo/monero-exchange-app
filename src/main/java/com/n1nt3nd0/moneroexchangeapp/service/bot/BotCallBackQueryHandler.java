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
                break;
            case "Сбербанк" :
                userChooseSberbankPaymentCommand(chat_id, message_id);
                break;
            case "Согласен" :
                createNewXmrOrder(username, chat_id, message_id);
                break;
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

    private void createNewXmrOrder(String username, long chatId, long messageId) {
        String text = "Время на оплату вашего заказа № 80475992 15 минут!\n" +
                "\n" +
                "Средства отправленные без заявки, возврату НЕ подлежат! Оплачивать вы должны ровно ту сумму, которая указана в заявке, иначе мы ваш платеж не найдем!  Все претензии по обмену принимаются в течении 24 часов. \n" +
                "Обращаем внимание: средства вы должны отправлять только со своей личной карты. Администрация может потребовать верификацию документов клиента или задержать обмен для проверки других данных.\n" +
                "\n" +
                "Для зачисления 0,50000000 XMR, Вам надо оплатить: 8 542 руб.\n" +
                "\n" +
                "Итого к оплате: 8 542 руб.\n" +
                "\n" +
                "После оплаты средства будут переведены на кошелек XMR 88rUszsaDgx9RBaL5e6dgcaLX64skLRfLdsVRS3s3zPmj7p3mtcfCuVV86R9F47kgJEJkXKHBKkM3NY7axwa4Cd6Ptx6hRg\n" +
                "\n" +
                "Если у вас есть вопрос, или возникли проблемы с оплатой, пишите поддержке: @MONOPOLYBANKER \n" +
                "\n" +
                "Реквизиты для оплаты - перевод СТРОГО на Сбербанк по номеру телефона:";
    }

    private void userChooseSberbankPaymentCommand(long chatId, long messageID) {
        String answer = "Скопируйте и отправьте боту свой кошелек XMR";
        daoBotState.savePaymentMethod(String.valueOf(chatId), "SBER");
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
                            .lastBotStateEnum(BotStateEnum.USER_CHOOSE_SBERBANK_PAYMENT)
                            .id(UUID.randomUUID().toString())
                            .build()
            );;
        } catch (TelegramApiException e) {
            log.error("error while update has callback query: " + e.getMessage());
            throw new RuntimeException();
        }

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
