package com.n1nt3nd0.moneroexchangeapp.service.bot;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static java.lang.Math.toIntExact;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramBot implements LongPollingSingleThreadUpdateConsumer {

    private final TelegramClient telegramClient;
    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            String username = update.getMessage().getChat().getUserName();
            Long chatId = update.getMessage().getChatId();
            if (text.equals("/start")){
                    startCommand(chatId, text, username);
            }
            else if (text.equals("/menu")){
                    buyCryptoCommand(chatId, text, username);
            }
        }

        else if (update.hasCallbackQuery()){
            // Set variables
            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();
            if (call_data.equals("monero")) {
                String answer = "Enter your xmr address: ";
                EditMessageText new_message = EditMessageText.builder()
                        .chatId(chat_id)
                        .messageId(toIntExact(message_id))
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
                } catch (TelegramApiException e) {
                    log.error("error while update has callback query: " + e.getMessage());
                    throw new RuntimeException();
                }
            }
        }
    }

    private void buyCryptoCommand(Long chatId, String text, String username) {
        SendMessage message = SendMessage // Create a message object object
                .builder()
                .chatId(chatId)
                .text("Select the command you want to buy")
                // Set the keyboard markup
                .replyMarkup(InlineKeyboardMarkup
                        .builder()
                        .keyboardRow(
                                new InlineKeyboardRow(InlineKeyboardButton
                                        .builder()
                                        .text("Monero")
                                        .callbackData("monero")
                                        .build(), InlineKeyboardButton
                                        .builder()
                                        .text("Btc")
                                        .callbackData("btc")
                                        .build(), InlineKeyboardButton
                                        .builder()
                                        .text("Eth")
                                        .callbackData("eth")
                                        .build()
                                )
                        )
                        .build())
                .build();
        try {
            telegramClient.execute(message);
        }catch (TelegramApiException exception){
            throw new RuntimeException("Telegram API exception while buy crypto command", exception);
        }

    }

    private void startCommand(Long chatId, String text, String username) {
       log.info("received new message: "+ text);
        String message = "Hello, bro %s".formatted(username);
        SendMessage sendMessage = new SendMessage(chatId.toString(), message);
        try {
        telegramClient.execute(sendMessage);
        }catch (TelegramApiException exception){
            throw new RuntimeException("Telegram API exception while start command", exception);
        }
    }
}
