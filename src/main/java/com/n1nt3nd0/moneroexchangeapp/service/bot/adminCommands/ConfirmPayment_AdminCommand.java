package com.n1nt3nd0.moneroexchangeapp.service.bot.adminCommands;


import com.n1nt3nd0.moneroexchangeapp.dao.DaoBotState;
import com.n1nt3nd0.moneroexchangeapp.dto.XmrOrderDto;
import com.n1nt3nd0.moneroexchangeapp.model.BotLastState;
import com.n1nt3nd0.moneroexchangeapp.model.XmrOrder;
import com.n1nt3nd0.moneroexchangeapp.model.bot_last_state.BotStateEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.UUID;

import static java.lang.Math.toIntExact;

@Component
@Slf4j
public class ConfirmPayment_AdminCommand implements AdminBotCommand{

    @Override
    public void execute(Update update, DaoBotState daoBotState, TelegramClient telegramClient, String username) {
        confirmPaymentCommand(update, daoBotState, telegramClient, username);
    }
    public void confirmPaymentCommand(Update update, DaoBotState daoBotState, TelegramClient telegramClient, String username){
        XmrOrderDto xmrOrderDto = daoBotState.getXmrOrderDto(username);
        String amountToBePaid = xmrOrderDto.getAmountToBePaid();
        XmrOrder orderHasMadeAPayment = daoBotState.getNewXmrExchangeOrder(username);
        String address = orderHasMadeAPayment.getAddress();
        Long chatId = orderHasMadeAPayment.getBotUser().getChatId();
        String message = "Внесена сумма %s RUB. \n".formatted(amountToBePaid) +
                "\n" +
                "Всего оплачено: %s\n".formatted(amountToBePaid) +
                "В ближайшие 5 минут средства будут отправлены на ваш кошелек."+ address + "\n" +
                "Бот уведомит вас об этом.";
//        EditMessageText new_message = EditMessageText.builder()
//                .chatId(chatId)
//                .messageId(toIntExact(messageId))
//                .text(message)
//                .build();
        SendMessage USER_SUCCESSFULLY_PAYMENT = SendMessage // Create a message object object
                .builder()
                .chatId(chatId)
                .text(message)
                // Set the keyboard markup
                .build();


        try {
            telegramClient.execute(USER_SUCCESSFULLY_PAYMENT);
            daoBotState.updateCurrentlyBotState(
                    BotLastState.builder()
                            .chatId(String.valueOf(chatId))
                            .lastBotStateEnum(BotStateEnum.ADMIN_CONFIRMED_XMR_EXCHANGE_ORDER)
                            .id(UUID.randomUUID().toString())
                            .build()
            );;
        } catch (TelegramApiException e) {
            log.error("error while confirmPaymentCommand: " + e.getMessage());
            throw new RuntimeException();
        }
//        String message = "Ваша заявка исполнена №10301640!\n" +
//                "  \n" +
//                "На ваш кошелек: 88rUszsaDgx9RBaL5e6dgcaLX64skLRfLdsVRS3s3zPmj7p3mtcfCuVV86R9F47kgJEJkXKHBKkM3NY7axwa4Cd6Ptx6hRg\n" +
//                "\n" +
//                "Отправлена сумма: 0,51110130 XMR\n" +
//                "\n" +
//                "Все обмены зачисляются после 5-15 подтверждений от сети, всё зависит от вашего сервиса, на котором расположен ваш кошелек.\n" +
//                "\n" +
//                "Время зачисления XMR при обычной загруженности в среднем от 20 минут.\n" +
//                "\n" +
//                "Процедура подтверждения НЕ ЗАВИСИТ от нас и определяется исключительно скоростью обработки транзакций криптовалютными сетями.\n" +
//                "\n" +
//                "\n" +
//                "Спасибо за обмен! Вам начислилось 16 руб. на внутренний баланс.";
//        SendMessage SEND_MESSAGE_TO_ADMIN = SendMessage // Create a message object object
//                .builder()
//                .chatId(7319257049L)
//                // Set the keyboard markup
//                .replyMarkup(InlineKeyboardMarkup
//                        .builder()
//                        .keyboard(List.of(
//                                new InlineKeyboardRow(InlineKeyboardButton
//                                        .builder()
//                                        .text("Подтвердить платеж")
//                                        .callbackData("Подтвердить платеж")
//                                        .build()
//                                ), new InlineKeyboardRow(InlineKeyboardButton
//                                        .builder()
//                                        .text("Платеж не найден")
//                                        .callbackData("Платеж не найден")
//                                        .build()
//                                )
//                        ))
//                        .build())
//                .text(message)
//                // Set the keyboard markup
//                .build();


    }
}
