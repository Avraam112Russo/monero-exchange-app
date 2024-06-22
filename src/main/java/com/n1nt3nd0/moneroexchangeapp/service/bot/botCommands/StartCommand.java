package com.n1nt3nd0.moneroexchangeapp.service.bot.botCommands;

import com.n1nt3nd0.moneroexchangeapp.dao.DaoBotState;
import com.n1nt3nd0.moneroexchangeapp.model.BotLastState;
import com.n1nt3nd0.moneroexchangeapp.model.BotUser;
import com.n1nt3nd0.moneroexchangeapp.model.bot_last_state.BotStateEnum;
import com.n1nt3nd0.moneroexchangeapp.repository.TelegramBotUserRepository;
import com.vdurmont.emoji.EmojiParser;
import lombok.RequiredArgsConstructor;
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

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
public class StartCommand implements BotCommand, Serializable {

    @Override
    public void execute(Update update,
                        TelegramClient telegramClient,
                        DaoBotState daoBotState,
                        TelegramBotUserRepository telegramBotUserRepository,
                        RestTemplate restTemplate

    ) {
        String text = update.getMessage().getText();
        String username = update.getMessage().getChat().getUserName();
        Long chatId = update.getMessage().getChatId();
        startCommand(telegramClient, daoBotState, telegramBotUserRepository, chatId, text, username);
    }

    private void startCommand(TelegramClient telegramClient,
                              DaoBotState daoBotState,
                              TelegramBotUserRepository telegramBotUserRepository,
                              Long chatId,
                              String text,
                              String username

    ) {
        log.info("received new message: " + text);
        Optional<BotUser> mayBeUser = telegramBotUserRepository.findTelegramBotUserByUsername(username);
        if (mayBeUser.isEmpty()){
            telegramBotUserRepository.save(
                    BotUser.builder()
                            .username(username)
                            .createdAt(LocalDateTime.now())
                            .chatId(chatId)
                            .build()
            );
            log.info("User {} saved in db successfully", username);
        }
        BotLastState lastState = BotLastState.builder()
                .id(UUID.randomUUID().toString())
                .chatId(String.valueOf(chatId))
                .lastBotStateEnum(BotStateEnum.START_COMMAND)
                .build();
        daoBotState.updateCurrentlyBotState(lastState);
        BotLastState botState = daoBotState.getCurrentlyBotState(chatId.toString());
        log.info(botState.toString());
        String answer = EmojiParser.parseToUnicode("Here is ✅ a smile emoji: :smile:\n\n Here is alien emoji: :alien:");
        SendMessage sendMessage = SendMessage // Create a message object object
                .builder()
                .chatId(chatId)
                .text("Бот обменник ✅\n" +
                        "\n" +
                        "Тут ты можешь обменять свои RUB на XMR\n" +
                        "\n" +
                        "Жми кнопку  Купить XMR или просто введи сумму в RUB или XMR\n" +
                        "\n" +
                        "Пример: 0.1 или 0,1 или 5030")
//                .text(answer)
                // Set the keyboard markup
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboard(List.of(
                                new InlineKeyboardRow(InlineKeyboardButton
                                        .builder()
                                        .text(EmojiParser.parseToUnicode(" \uD83D\uDC49 Купить xmr \uD83D\uDC48"))
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
}
