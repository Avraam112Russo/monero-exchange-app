package com.n1nt3nd0.moneroexchangeapp.service.bot.botCommands;

import com.n1nt3nd0.moneroexchangeapp.dao.DaoBotState;
import com.n1nt3nd0.moneroexchangeapp.model.BotLastState;
import com.n1nt3nd0.moneroexchangeapp.model.XmrOrder;
import com.n1nt3nd0.moneroexchangeapp.model.bot_last_state.BotStateEnum;
import com.n1nt3nd0.moneroexchangeapp.repository.TelegramBotUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
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
public class UserMadePaymentCommand implements BotCommand{
    @Override
    public void execute(Update update, TelegramClient telegramClient, DaoBotState daoBotState, TelegramBotUserRepository telegramBotUserRepository, RestTemplate restTemplate) {
        userMadePaymentCommand(update, telegramClient, daoBotState,  telegramBotUserRepository, restTemplate);
    }
    private void userMadePaymentCommand(Update update, TelegramClient telegramClient, DaoBotState daoBotState, TelegramBotUserRepository telegramBotUserRepository, RestTemplate restTemplate){
        String username = update.getCallbackQuery().getMessage().getChat().getUserName();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        String answer =  "Идет проверка оплаты. Пожалуйста. ожидайте. \n" +
                "\n" +
                "Будьте внимательны!Если вы отправили неверную сумму или выполнили платеж частично, " +
                "то средства будут безвовзратно утеряны.";;
        EditMessageText new_message = EditMessageText.builder()
                .chatId(chatId)
                .messageId(toIntExact(messageId))
                .text(answer)
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .build())
                .build();

        XmrOrder newXmrExchangeOrder = daoBotState.getNewXmrExchangeOrder(username);
        String paymentMethod = newXmrExchangeOrder.getPayMentMethod().name();
        String amountToBePaid = newXmrExchangeOrder.getPriceInRuble().toString();
        String xmrQuantity = newXmrExchangeOrder.getQuantity().toString();
        String xmrAddress = newXmrExchangeOrder.getAddress();
        String message = "User made payment. Please, check transaction and send to coins. \n" +
                "\n" +
                "Payment method: " + paymentMethod + "\n" +
                "Amount to be paid: " + amountToBePaid + "\n" +
                "Username: " + username + "\n" +
                "XMR quamtity: " + xmrQuantity + "\n" +
                "Xmr address: " + xmrAddress + "\n" +
                "";

        SendMessage SEND_MESSAGE_TO_ADMIN = SendMessage // Create a message object object
                .builder()
                .chatId(7319257049L)
                .text(message)
                // Set the keyboard markup
                .build();


        try {
            telegramClient.execute(new_message);
            telegramClient.execute(SEND_MESSAGE_TO_ADMIN);
            log.info("Send message to admin with successfully! ");
            daoBotState.updateCurrentlyBotState(
                    BotLastState.builder()
                            .chatId(String.valueOf(chatId))
                            .lastBotStateEnum(BotStateEnum.USER_MADE_PAYMENT)
                            .id(UUID.randomUUID().toString())
                            .build()
            );;
        } catch (TelegramApiException e) {
            log.error("error while update has callback query: " + e.getMessage());
            throw new RuntimeException();
        }
    }
}