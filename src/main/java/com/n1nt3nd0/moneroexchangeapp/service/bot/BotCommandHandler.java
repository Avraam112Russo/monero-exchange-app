package com.n1nt3nd0.moneroexchangeapp.service.bot;

import com.n1nt3nd0.moneroexchangeapp.model.TelegramBotUser;
import com.n1nt3nd0.moneroexchangeapp.redis.RedisStorageTelegramBot;
import com.n1nt3nd0.moneroexchangeapp.repository.TelegramBotUserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class BotCommandHandler {
    private final TelegramClient telegramClient;
    private final RedisStorageTelegramBot redisStorage;
    private final TelegramBotUserRepository telegramBotUserRepository;
    public void startCommand(Long chatId, String text, String username, String firstName, String lastName) {
        log.info("received new message: " + text);

        Optional<TelegramBotUser> telegramBotUserByUsername = telegramBotUserRepository.findTelegramBotUserByUsername(username);
        if (telegramBotUserByUsername.isEmpty()){
            telegramBotUserRepository.save(
                    TelegramBotUser.builder()
                            .username(username)
                            .firstName(firstName)
                            .lastName(lastName)
                            .createdAt(LocalDateTime.now())
                            .build()
            );
        }


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
//        redisStorage.saveUser(username, firstName);
        try {
            telegramClient.execute(sendMessage);
        } catch (TelegramApiException exception) {
            throw new RuntimeException("Telegram API exception while start command", exception);
        }
    }

    public void completeCheckoutCommand(Long chatId, String username, Double amount, String marketPriceUsd) {
        redisStorage.saveXmrOrder(username, amount);
        double marketPriceRub = Double.parseDouble(marketPriceUsd) * 92;

        String messageText = "Средний рыночный курс XMR: " + marketPriceUsd + " USD, " + marketPriceRub + " RUB\n" +
                "\n" +
                "Вы получите: " + amount + " xmr\n" +
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
                                        .text("Сбербанк")
                                        .callbackData("Сбербанк")
                                        .build()
                                ), new InlineKeyboardRow(InlineKeyboardButton
                                        .builder()
                                        .text("Тинькоф")
                                        .callbackData("Тинькоф")
                                        .build()
                                ),
                                new InlineKeyboardRow(InlineKeyboardButton
                                        .builder()
                                        .text("Альфа банк")
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
        } catch (TelegramApiException exception) {
            throw new RuntimeException("Telegram API exception while start command", exception);
        }
    }


}