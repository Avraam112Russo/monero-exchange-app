package com.n1nt3nd0.moneroexchangeapp.service.bot.botCommands;

import com.n1nt3nd0.moneroexchangeapp.dao.DaoBotState;
import com.n1nt3nd0.moneroexchangeapp.dto.XmrOrderDto;
import com.n1nt3nd0.moneroexchangeapp.model.BotLastState;
import com.n1nt3nd0.moneroexchangeapp.model.PaymentMethod;
import com.n1nt3nd0.moneroexchangeapp.model.XmrOrder;
import com.n1nt3nd0.moneroexchangeapp.model.bot_last_state.BotStateEnum;
import com.n1nt3nd0.moneroexchangeapp.repository.TelegramBotUserRepository;
import com.vdurmont.emoji.EmojiParser;
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
                .build();

        XmrOrder newXmrExchangeOrder = daoBotState.getNewXmrExchangeOrder(username);
        String paymentMethod = newXmrExchangeOrder.getPayMentMethod().name();
        String amountToBePaid = newXmrExchangeOrder.getPriceInRuble().toString();
        String xmrQuantity = newXmrExchangeOrder.getQuantity().toString();
        String xmrAddress = newXmrExchangeOrder.getAddress();
        Long orderId = newXmrExchangeOrder.getId();
        String message = "The User has made a payment. Please, check the transaction and send the coins. \n" +
                "\n" +
                "Payment method: " + paymentMethod + "\n" +
                "Amount to be paid: " + amountToBePaid + "\n" +
                "Username: " + username + "\n" +
                "XMR quantity: " + xmrQuantity + "\n" +
                "Xmr address: " + xmrAddress + "\n" +
                "Order id: " + orderId +
                "";
        XmrOrderDto orderDto = XmrOrderDto.builder()
                .chatId(chatId)
                .orderId(orderId)
                .paymentMethod(PaymentMethod.valueOf(paymentMethod))
                .username(username)
                .xmrQuantity(Double.parseDouble(xmrQuantity))
                .amountToBePaid(amountToBePaid)
                .xmrAddressForSending(xmrAddress)
                .build();
        daoBotState.putXmrOrderDto(orderDto);


        SendMessage SEND_MESSAGE_TO_ADMIN = SendMessage // Create a message object object
                .builder()
                .chatId(7319257049L)
//                // Set the keyboard markup
//                .replyMarkup(InlineKeyboardMarkup
//                        .builder()
//                        .keyboard(List.of(
//                                new InlineKeyboardRow(InlineKeyboardButton
//                                        .builder()
//                                        .text("Подтвердить платеж")
//                                        .callbackData("/confirmPaymentAdminCommand")
//                                        .build()
//                                ), new InlineKeyboardRow(InlineKeyboardButton
//                                        .builder()
//                                        .text("Платеж не найден")
//                                        .callbackData("Платеж не найден")
//                                        .build()
//                                )
//                        ))
//                        .build())
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
