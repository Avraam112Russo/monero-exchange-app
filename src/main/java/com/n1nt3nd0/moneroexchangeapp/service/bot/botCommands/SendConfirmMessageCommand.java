package com.n1nt3nd0.moneroexchangeapp.service.bot.botCommands;


import com.n1nt3nd0.moneroexchangeapp.dao.DaoBotState;
import com.n1nt3nd0.moneroexchangeapp.model.BotLastState;
import com.n1nt3nd0.moneroexchangeapp.model.bot_last_state.BotStateEnum;
import com.n1nt3nd0.moneroexchangeapp.repository.TelegramBotUserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.util.List;
import java.util.UUID;

@Component
@Slf4j
public class SendConfirmMessageCommand implements BotCommand{
    @Override
    public void execute(Update update,
                        TelegramClient telegramClient,
                        DaoBotState daoBotState,
                        TelegramBotUserRepository telegramBotUserRepository, RestTemplate restTemplate) {

        sendingConfirmMessage(update, telegramClient, daoBotState, telegramBotUserRepository, restTemplate);
    }
    private void sendingConfirmMessage(Update update,
                                       TelegramClient telegramClient,
                                       DaoBotState daoBotState,
                                       TelegramBotUserRepository telegramBotUserRepository, RestTemplate restTemplate) {

        long chatId = update.getMessage().getChatId();
        Double amountXmrUserWantBuy = daoBotState.getAmountXmrUserWantBuy(String.valueOf(chatId));
        String address = update.getMessage().getText();
        daoBotState.saveUserXmrAddress(address, String.valueOf(chatId));
        String message =
                "Время на оплату заказа 15 минут! \n" +
                        "\n" +
                        "Средства отправленные без заявки, возврату НЕ подлежат! Оплачивать вы должны ровно ту сумму, которая указана в заявке, иначе мы ваш платеж не найдем!  Все претензии по обмену принимаются в течении 24 часов. \n" +
                        "Обращаем внимание: средства вы должны отправлять только со своей личной карты. Администрация может потребовать верификацию документов клиента или задержать обмен для проверки других данных.\n" +
                        "\n" +
                        "Итого к оплате: "+ amountXmrUserWantBuy * 159 * 92 +" RUB \n" +
                        "\n" +
                        "ВНИМАТЕЛЬНО сверяйте адрес своего кошелька!\n" +
                        "\n" +
                        "После оплаты средства будут переведены на кошелек: " +address + "\n" +
                        "\n" +
                        " \n" +
                        "\n" +
                        "Если у вас есть вопрос, или возникли проблемы с оплатой, пишите поддержке: @BINGO_SUPPORT\n" +
                        "\n" +
                        "Вы согласны на обмен?";


        SendMessage sendMessage = SendMessage // Create a message object object
                .builder()
                .chatId(chatId)
                .text(message)
                // Set the keyboard markup
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboard(List.of(
                                new InlineKeyboardRow(InlineKeyboardButton
                                        .builder()
                                        .text("Согласен")
                                        .callbackData("/Согласен")
                                        .build()
                                ), new InlineKeyboardRow(InlineKeyboardButton
                                        .builder()
                                        .text("Отмена")
                                        .callbackData("Отмена")
                                        .build()
                                ),
                                new InlineKeyboardRow(InlineKeyboardButton
                                        .builder()
                                        .text("Списать с баланса 16 RUB")
                                        .callbackData("Списать с баланса")
                                        .build()
                                )
                        ))
//                        .keyboardRow(
//                                new InlineKeyboardRow(InlineKeyboardButton
//                                        .builder()
//                                        .text("Купить xmr")
//                                        .callbackData("Купить xmr")
//                                        .build(), InlineKeyboardButton
//                                        .builder()
//                                        .text("Партнерская программа")
//                                        .callbackData("Партнерская программа")
//                                        .build()
//                                )
//                        )
                        .build())
                .build();
        try {
            telegramClient.execute(sendMessage);
            daoBotState.updateCurrentlyBotState(
                    BotLastState.builder()
                            .chatId(String.valueOf(chatId))
                            .lastBotStateEnum(BotStateEnum.USER_TYPE_XMR_ADDRESS)
                            .id(UUID.randomUUID().toString())
                            .build()
            );;
        } catch (TelegramApiException exception) {
            throw new RuntimeException("Telegram API exception while start command", exception);
        }

    }

}
