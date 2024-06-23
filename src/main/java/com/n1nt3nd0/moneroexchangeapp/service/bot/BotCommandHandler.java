package com.n1nt3nd0.moneroexchangeapp.service.bot;

import com.n1nt3nd0.moneroexchangeapp.model.BotLastState;
import com.n1nt3nd0.moneroexchangeapp.model.BotUser;
import com.n1nt3nd0.moneroexchangeapp.model.bot_last_state.BotStateEnum;
import com.n1nt3nd0.moneroexchangeapp.dao.DaoBotState;
import com.n1nt3nd0.moneroexchangeapp.repository.TelegramBotUserRepository;
import com.n1nt3nd0.moneroexchangeapp.service.BlockChairApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class BotCommandHandler {
    private final TelegramClient telegramClient;
    private final DaoBotState redisStorage;
    private final TelegramBotUserRepository telegramBotUserRepository;
    private final BlockChairApi blockChairApi;
    public void startCommand(Long chatId, String text, String username) {
        log.info("received new message: " + text);
        Optional<BotUser> mayBeUser = telegramBotUserRepository.findTelegramBotUserByUsername(username);
        if (mayBeUser.isEmpty()){
            telegramBotUserRepository.save(
                    BotUser.builder()
                            .username(username)
                            .createdAt(LocalDateTime.now())
                            .build()
            );
            log.info("User {} saved in db successfully", username);
        }
        BotLastState lastState = BotLastState.builder()
                .id(UUID.randomUUID().toString())
                .chatId(String.valueOf(chatId))
                .lastBotStateEnum(BotStateEnum.START_COMMAND)
                .build();
        redisStorage.updateCurrentlyBotState(lastState);
        BotLastState botState = redisStorage.getCurrentlyBotState(chatId.toString());
        log.info(botState.toString());
        SendMessage sendMessage = SendMessage // Create a message object object
                .builder()
                .chatId(chatId)
                .text("Бот обменник\n" +
                        "\n" +
                        "Тут ты можешь обменять свои RUB на XMR\n" +
                        "\n" +
                        "Жми кнопку  Купить XMR или просто введи сумму в RUB или XMR\n" +
                        "\n" +
                        "Пример: 0.1 или 0,1 или 5030")
                // Set the keyboard markup
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboard(List.of(
                                new InlineKeyboardRow(InlineKeyboardButton
                                        .builder()
                                        .text("Купить xmr")
                                        .callbackData("Купить xmr")
                                        .build()
                                ), new InlineKeyboardRow(InlineKeyboardButton
                                        .builder()
                                        .text("Партнерская программа")
                                        .callbackData("Партнерская программа")
                                        .build()
                                ),
                                new InlineKeyboardRow(InlineKeyboardButton
                                        .builder()
                                        .text("Поддержка / Оператор")
                                        .callbackData("Поддержка / Оператор")
                                        .build(), InlineKeyboardButton
                                        .builder()
                                        .text("Отзывы")
                                        .callbackData("Отзывы")
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
        } catch (TelegramApiException exception) {
            throw new RuntimeException("Telegram API exception while start command", exception);
        }
    }

    public void sendingConfirmMessage(long chatId, String text, String username) {
        Double amountXmrUserWantBuy = redisStorage.getAmountXmrUserWantBuy(String.valueOf(chatId));
        String message =
                "Время на оплату заказа 15 минут! \n" +
                        "\n" +
                        "Средства отправленные без заявки, возврату НЕ подлежат! Оплачивать вы должны ровно ту сумму, которая указана в заявке, иначе мы ваш платеж не найдем!  Все претензии по обмену принимаются в течении 24 часов. \n" +
                        "Обращаем внимание: средства вы должны отправлять только со своей личной карты. Администрация может потребовать верификацию документов клиента или задержать обмен для проверки других данных.\n" +
                        "\n" +
                        "Итого к оплате: "+ amountXmrUserWantBuy +"\n" +
                        "\n" +
                        "ВНИМАТЕЛЬНО сверяйте адрес своего кошелька!\n" +
                        "\n" +
                        "После оплаты средства будут переведены на кошелек: " + text + "\n" +
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
                                        .callbackData("Согласен")
                                        .build()
                                ), new InlineKeyboardRow(InlineKeyboardButton
                                        .builder()
                                        .text("Отмена")
                                        .callbackData("Отмена сделки")
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
            redisStorage.updateCurrentlyBotState(
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


    public void stateCommandHandle(String chatId) {
        BotLastState currentlyBotState = redisStorage.getCurrentlyBotState(chatId);
        log.info(currentlyBotState.toString());
    }

    public void userTypeXmrAmountWantBuy(String text, Long chatId, String username) {
        redisStorage.putAmountXmrUserWantBuy(chatId.toString(), Double.parseDouble(text), username);
        String marketPriceUsd = blockChairApi.fetchLastMarketPriceXmr();
        double marketPriceRub = Double.parseDouble(marketPriceUsd) * 92; // 15.970, 283729378293
        Double truncatedMarketPriceRub = BigDecimal.valueOf(marketPriceRub).setScale(3, RoundingMode.HALF_UP).doubleValue(); // 15.970, 283
        Double checkOutSum = Double.parseDouble(text) * marketPriceRub;
        String messageText = "Средний рыночный курс XMR: " + marketPriceUsd + " USD, " + truncatedMarketPriceRub + " RUB\n" +
                "\n" +
                "Вы получите: " + text + " xmr\n" +
                "\n" +
                "Ваш внутренний баланс кошелька: 17 руб.\n" +
                "\n" +
                "Для продолжения выберите Способ оплаты:";

        SendMessage sendMessage = SendMessage // Create a message object object
                .builder()
                .chatId(chatId)
                .text(messageText)
                // Set the keyboard markup
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboard(List.of(
                                new InlineKeyboardRow(InlineKeyboardButton
                                        .builder()
                                        .text("Сбербанк (%s) RUB.".formatted(checkOutSum))
                                        .callbackData("Сбербанк")
                                        .build()
                                ), new InlineKeyboardRow(InlineKeyboardButton
                                        .builder()
                                        .text("Тинькоф (%s) RUB.".formatted(checkOutSum))
                                        .callbackData("Тинькоф")
                                        .build()
                                ),
                                new InlineKeyboardRow(InlineKeyboardButton
                                        .builder()
                                        .text("Альфа Банк (%s) RUB.".formatted(checkOutSum))
                                        .callbackData("Альфа банк")
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
            redisStorage.updateCurrentlyBotState(
                    BotLastState.builder()
                            .chatId(String.valueOf(chatId))
                            .lastBotStateEnum(BotStateEnum.USER_TYPE_AMOUNT_XMR_WANT_BUY)
                            .id(UUID.randomUUID().toString())
                            .build()
            );;
        } catch (TelegramApiException exception) {
            throw new RuntimeException("Telegram API exception while start command", exception);
        }
    }

    public void saveUserXmrAddress(String text, long chatID, String username) {
        redisStorage.saveUserXmrAddress(text, String.valueOf(chatID));
        sendingConfirmMessage(chatID, text, username);
    }
}