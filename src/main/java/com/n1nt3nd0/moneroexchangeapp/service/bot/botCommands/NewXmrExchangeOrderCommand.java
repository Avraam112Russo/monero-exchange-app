package com.n1nt3nd0.moneroexchangeapp.service.bot.botCommands;

import com.n1nt3nd0.moneroexchangeapp.dao.DaoBotState;
import com.n1nt3nd0.moneroexchangeapp.model.BotLastState;
import com.n1nt3nd0.moneroexchangeapp.model.PaymentMethod;
import com.n1nt3nd0.moneroexchangeapp.model.XmrOrder;
import com.n1nt3nd0.moneroexchangeapp.model.bot_last_state.BotStateEnum;
import com.n1nt3nd0.moneroexchangeapp.repository.TelegramBotUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
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

public class NewXmrExchangeOrderCommand implements BotCommand{
    @Override
    public void execute(Update update, TelegramClient telegramClient, DaoBotState daoBotState, TelegramBotUserRepository telegramBotUserRepository, RestTemplate restTemplate) {
        createNewXmrOrder( update, telegramClient, daoBotState, telegramBotUserRepository, restTemplate);
    }
    private void createNewXmrOrder(Update update, TelegramClient telegramClient, DaoBotState daoBotState, TelegramBotUserRepository telegramBotUserRepository, RestTemplate restTemplate) {
        Long chatId = update.getCallbackQuery().getMessage().getChatId();
        double quantityXmrOrder = daoBotState.getAmountXmrUserWantBuy(String.valueOf(chatId));
        double currentlyMarketPrice = daoBotState.getCurrentlyXmrMarketPrice(chatId.toString());
        double sumForPayInRuble = currentlyMarketPrice * quantityXmrOrder * 92;
        String address = daoBotState.getUserXmrAddress(chatId.toString());
        String username = update.getCallbackQuery().getMessage().getChat().getUserName();
        Integer messageId = update.getCallbackQuery().getMessage().getMessageId();
        String paymentMethod = daoBotState.getPaymentMethod(chatId.toString());
        XmrOrder order = XmrOrder.builder()
                .address(address)
                .botUser(telegramBotUserRepository.findTelegramBotUserByUsername(username).get())
                .payMentMethod(PaymentMethod.valueOf(paymentMethod))
                .quantity(quantityXmrOrder)
                .priceInRuble(sumForPayInRuble)
                .build();
        daoBotState.saveNewXmrExchangeOrder(order);

        int orderId = 109230923;
        String text = "Время на оплату вашего заказа № " + orderId + " 15 минут!\n" +
                "\n" +
                "Средства отправленные без заявки, возврату НЕ подлежат! Оплачивать вы должны ровно ту сумму, которая указана в заявке, иначе мы ваш платеж не найдем!  Все претензии по обмену принимаются в течении 24 часов. \n" +
                "Обращаем внимание: средства вы должны отправлять только со своей личной карты. Администрация может потребовать верификацию документов клиента или задержать обмен для проверки других данных.\n" +
                "\n" +
                "Для зачисления " + quantityXmrOrder + " XMR, Вам надо оплатить: " +  sumForPayInRuble + " RUB\n" +
                "\n" +
                "Итого к оплате: " +  sumForPayInRuble + " RUB\n" +
                "\n" +
                "После оплаты средства будут переведены на кошелек XMR: " + address + "\n" +
                "\n" +
                "Если у вас есть вопрос, или возникли проблемы с оплатой, пишите поддержке: @BINGO_SUPPORT \n" +
                "\n" +
                "Реквизиты для оплаты - перевод СТРОГО на Сбербанк по номеру телефона: 89241560676";

            EditMessageText new_message = EditMessageText.builder()
                    .chatId(chatId)
                    .messageId(toIntExact(messageId))
                    .text(text)
                    .replyMarkup(InlineKeyboardMarkup
                            .builder()
                            .keyboard(List.of(

                                    new InlineKeyboardRow(InlineKeyboardButton
                                            .builder()
                                            .text("Я оплатил")
                                            .callbackData("Я оплатил")
                                            .build()
                                    ),
                                            new InlineKeyboardRow(InlineKeyboardButton
                                                    .builder()
                                                    .text("Отмена")
                                                    .callbackData("Отмена")
                                                    .build()
                                            )
                            )
                            )
                            .build())
                    .build();
            try {
                telegramClient.execute(new_message);
                daoBotState.updateCurrentlyBotState(
                        BotLastState.builder()
                                .chatId(String.valueOf(chatId))
                                .lastBotStateEnum(BotStateEnum.USER_CREATED_NEW_XMR_EXCHANGE_ORDER)
                                .id(UUID.randomUUID().toString())
                                .build()
                );;
            } catch (TelegramApiException e) {
                log.error("error while USER_CREATED_NEW_XMR_EXCHANGE_ORDER: " + e.getMessage());
                throw new RuntimeException();
            }

    }
}
