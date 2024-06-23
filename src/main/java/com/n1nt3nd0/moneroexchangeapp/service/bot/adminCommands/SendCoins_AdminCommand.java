package com.n1nt3nd0.moneroexchangeapp.service.bot.adminCommands;

import com.n1nt3nd0.moneroexchangeapp.dao.DaoBotState;
import com.n1nt3nd0.moneroexchangeapp.dto.XmrOrderDto;
import com.n1nt3nd0.moneroexchangeapp.model.BotLastState;
import com.n1nt3nd0.moneroexchangeapp.model.bot_last_state.BotStateEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.UUID;

@Component
@Slf4j
public class SendCoins_AdminCommand implements AdminBotCommand {

    @Override
    public void execute(Update update, DaoBotState daoBotState, TelegramClient telegramClient, String username) {

        sendCoinsAdminCommand( update,  daoBotState,  telegramClient,  username);
    }
    public void sendCoinsAdminCommand(Update update, DaoBotState daoBotState, TelegramClient telegramClient, String username){

        XmrOrderDto xmrOrderDto = daoBotState.getXmrOrderDto(username);
        Long orderId = xmrOrderDto.getOrderId();
        String address = xmrOrderDto.getXmrAddressForSending();
        double xmrQuantity = xmrOrderDto.getXmrQuantity();
        long chatId = xmrOrderDto.getChatId();
        String message = "Ваша заявка №" + orderId + " исполнена \n" +
                "  \n" +
                "На ваш кошелек: " + address + "\n" +
                "\n" +
                "Отправлена сумма: " + xmrQuantity + " XMR \n" +
                "\n" +
                "Все обмены зачисляются после 5-15 подтверждений от сети, всё зависит от вашего сервиса, на котором расположен ваш кошелек.\n" +
                "\n" +
                "Время зачисления XMR при обычной загруженности в среднем от 20 минут.\n" +
                "\n" +
                "Процедура подтверждения НЕ ЗАВИСИТ от нас и определяется исключительно скоростью обработки транзакций криптовалютными сетями.\n" +
                "\n" +
                "\n" +
                "Спасибо за обмен! Вам начислилось 16 руб. на внутренний баланс.";


        SendMessage COMPLETE_ORDER_MESSAGE = SendMessage // Create a message object object
                .builder()
                .chatId(chatId)
                .text(message)
                // Set the keyboard markup
                .build();


        try {
            telegramClient.execute(COMPLETE_ORDER_MESSAGE);
            daoBotState.updateCurrentlyBotState(
                    BotLastState.builder()
                            .chatId(String.valueOf(chatId))
                            .lastBotStateEnum(BotStateEnum.ORDER_SUCCESSFULLY_COMPLETE)
                            .id(UUID.randomUUID().toString())
                            .build()
            );;
        } catch (TelegramApiException e) {
            log.error("error while sendCoinsAdminCommand: " + e.getMessage());
            throw new RuntimeException();
        }
    }
}
