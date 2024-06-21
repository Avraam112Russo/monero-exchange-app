package com.n1nt3nd0.moneroexchangeapp.service.bot.botCommands;

import com.n1nt3nd0.moneroexchangeapp.dao.DaoBotState;
import com.n1nt3nd0.moneroexchangeapp.model.BotLastState;
import com.n1nt3nd0.moneroexchangeapp.model.BotUser;
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

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static java.lang.Math.toIntExact;

@Component
@Slf4j
public class BuyMoneroCommand implements BotCommand{
    @Override
    public void execute(Update update,
                        TelegramClient telegramClient,
                        DaoBotState daoBotState,
                        TelegramBotUserRepository telegramBotUserRepository,
                        RestTemplate restTemplate

    ) {

        String call_data = update.getCallbackQuery().getData();
        long message_id = update.getCallbackQuery().getMessage().getMessageId();
        long chatId = update.getCallbackQuery().getMessage().getChatId();
        String username = update.getCallbackQuery().getMessage().getChat().getUserName();
        buyMoneroCommand(telegramClient, daoBotState, telegramBotUserRepository, chatId, message_id);
    }

    private void buyMoneroCommand(TelegramClient telegramClient,
                              DaoBotState daoBotState,
                              TelegramBotUserRepository telegramBotUserRepository,
                              Long chatId,
                              Long messageId

    ) {
        String answer = "Укажите сумму в BTC или же RUB:\n" +
                "\n" +
                "Пример: 0.001 или 0,001 или 5030 ";
        EditMessageText new_message = EditMessageText.builder()
                .chatId(chatId)
                .messageId(toIntExact(messageId))
                .text(answer)
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboardRow(
                                new InlineKeyboardRow(InlineKeyboardButton
                                        .builder()
                                        .text("Отмена")
                                        .callbackData("Отмена")
                                        .build()
                                )
                        )
                        .build())
                .build();
        try {
            telegramClient.execute(new_message);
            daoBotState.updateCurrentlyBotState(
                    BotLastState.builder()
                            .chatId(String.valueOf(chatId))
                            .lastBotStateEnum(BotStateEnum.BUY_XMR_COMMAND)
                            .id(UUID.randomUUID().toString())
                            .build()
            );;
        } catch (TelegramApiException e) {
            log.error("error while update has callback query: " + e.getMessage());
            throw new RuntimeException();
        }
}

}
