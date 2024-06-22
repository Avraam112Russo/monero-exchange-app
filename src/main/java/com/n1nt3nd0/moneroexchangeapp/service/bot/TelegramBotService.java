package com.n1nt3nd0.moneroexchangeapp.service.bot;

import com.n1nt3nd0.moneroexchangeapp.dao.DaoBotState;
import com.n1nt3nd0.moneroexchangeapp.model.BotLastState;
import com.n1nt3nd0.moneroexchangeapp.model.bot_last_state.BotStateEnum;
import com.n1nt3nd0.moneroexchangeapp.repository.TelegramBotUserRepository;
import com.n1nt3nd0.moneroexchangeapp.service.bot.botCommands.BotCommand;
import com.n1nt3nd0.moneroexchangeapp.service.bot.botCommands.UserTypeAmountXmrCommand;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import static com.n1nt3nd0.moneroexchangeapp.model.bot_last_state.BotStateEnum.BUY_XMR_COMMAND;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramBotService implements LongPollingSingleThreadUpdateConsumer {
    private final TelegramClient telegramClient;
    private final DaoBotState redisStorage;
    private final TelegramBotUserRepository telegramBotUserRepository;
    private final BotCommandHandler botCommandHandler;
    private final BotCallBackQueryHandler botCallBackQueryHandler;
    private final RedisTemplate<String, Object> redisTemplate;
    private final DaoBotState daoBotState;
    private final RestTemplate restTemplate;
    @SneakyThrows
    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            Long chatId = update.getMessage().getChatId();
            BotLastState currentlyBotState = daoBotState.getCurrentlyBotState(String.valueOf(chatId));
            if (currentlyBotState.getLastBotStateEnum().equals(BotStateEnum.USER_CHOOSE_SBERBANK_PAYMENT) && text.length() > 30) {
                BotCommand command = (BotCommand) redisTemplate.opsForHash().get("bot_commands", "/confirm_message");

                if (command != null) {
                    command.execute(update, telegramClient, daoBotState, telegramBotUserRepository, restTemplate);
                }

            }
            if (update.hasMessage() && update.getMessage().hasText() && update.getMessage().getChatId() == 7319257049L){ // handle admin commands

            }


            BotCommand command = (BotCommand) redisTemplate.opsForHash().get("bot_commands", text);
            if (command != null){
                command.execute(update, telegramClient, daoBotState, telegramBotUserRepository, restTemplate);
            }


            boolean isNumeric = isNumeric(text);
            UserTypeAmountXmrCommand user_type_xmr_amount = (UserTypeAmountXmrCommand) redisTemplate.opsForHash().get("bot_commands", "/user_type_xmr_amount");
            if (isNumeric && user_type_xmr_amount != null) {
                user_type_xmr_amount.execute(update, telegramClient, daoBotState, telegramBotUserRepository, restTemplate);
            }


        }
        if (update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();
            BotCommand command = (BotCommand) redisTemplate.opsForHash().get("bot_commands", call_data);
            if (command != null){
                command.execute(update, telegramClient, daoBotState, telegramBotUserRepository, restTemplate);
            }
        }
    }

    public boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }

    }
