package com.n1nt3nd0.moneroexchangeapp.service.bot.adminCommands;


import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;

import java.util.List;

@Component
@Slf4j
public class ConfirmPayment_AdminCommand implements AdminBotCommand{

    @Override
    public void execute(Update update) {
        confirmPaymentCommand(update);
    }
    public void confirmPaymentCommand(Update update){
        String username = update.getCallbackQuery().getMessage().getChat().getUserName();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String call_data = update.getCallbackQuery().getData();
        String message = "Ваша заявка исполнена №10301640!\n" +
                "  \n" +
                "На ваш кошелек: 88rUszsaDgx9RBaL5e6dgcaLX64skLRfLdsVRS3s3zPmj7p3mtcfCuVV86R9F47kgJEJkXKHBKkM3NY7axwa4Cd6Ptx6hRg\n" +
                "\n" +
                "Отправлена сумма: 0,51110130 XMR\n" +
                "\n" +
                "Все обмены зачисляются после 5-15 подтверждений от сети, всё зависит от вашего сервиса, на котором расположен ваш кошелек.\n" +
                "\n" +
                "Время зачисления XMR при обычной загруженности в среднем от 20 минут.\n" +
                "\n" +
                "Процедура подтверждения НЕ ЗАВИСИТ от нас и определяется исключительно скоростью обработки транзакций криптовалютными сетями.\n" +
                "\n" +
                "\n" +
                "Спасибо за обмен! Вам начислилось 16 руб. на внутренний баланс.";
        SendMessage SEND_MESSAGE_TO_ADMIN = SendMessage // Create a message object object
                .builder()
                .chatId(7319257049L)
                // Set the keyboard markup
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboard(List.of(
                                new InlineKeyboardRow(InlineKeyboardButton
                                        .builder()
                                        .text("Подтвердить платеж")
                                        .callbackData("Подтвердить платеж")
                                        .build()
                                ), new InlineKeyboardRow(InlineKeyboardButton
                                        .builder()
                                        .text("Платеж не найден")
                                        .callbackData("Платеж не найден")
                                        .build()
                                )
                        ))
                        .build())
                .text(message)
                // Set the keyboard markup
                .build();


    }
}
