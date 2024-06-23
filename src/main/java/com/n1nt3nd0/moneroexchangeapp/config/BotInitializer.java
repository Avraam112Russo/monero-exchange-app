package com.n1nt3nd0.moneroexchangeapp.config;

import com.n1nt3nd0.moneroexchangeapp.model.bot_last_state.BotStateEnum;
import com.n1nt3nd0.moneroexchangeapp.service.bot.TelegramBotService;
import com.n1nt3nd0.moneroexchangeapp.service.bot.adminCommands.ConfirmPayment_AdminCommand;
import com.n1nt3nd0.moneroexchangeapp.service.bot.adminCommands.SendCoins_AdminCommand;
import com.n1nt3nd0.moneroexchangeapp.service.bot.botCommands.*;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.longpolling.TelegramBotsLongPollingApplication;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
@RequiredArgsConstructor
public class BotInitializer {
    private final TelegramBotService telegramBot;
    private final RedisTemplate<String, Object> redisTemplate;
    private final StartCommand startCommand;
    private final BuyMoneroCommand buyMoneroCommand;
    private final UserTypeAmountXmrCommand userTypeAmountXmrCommand;
    private final UserChooseSberbankCommand userChooseSberbankCommand;
    private final SendConfirmMessageCommand sendConfirmMessageCommand;
    private final NewXmrExchangeOrderCommand newXmrExchangeOrderCommand;
    private final UserMadePaymentCommand userMadePaymentCommand;

    private final ConfirmPayment_AdminCommand confirmPaymentAdminCommand;
    private final SendCoins_AdminCommand sendCoinsAdminCommand;
    @PostConstruct
    public void init() {
        try {
            String botToken = "7438604237:AAH-rHbOYV5xQ1eamxwSKa9XdUuRM_hysAs";
            String botCommands = "bot_commands";
             TelegramBotsLongPollingApplication botsApplication =
                    new TelegramBotsLongPollingApplication();
            botsApplication.registerBot(botToken, telegramBot);
            // botCommands 1 = value 2
            redisTemplate.opsForHash().put(botCommands, "/start", startCommand);
            redisTemplate.opsForHash().put(botCommands, "/buy_monero", buyMoneroCommand);
            redisTemplate.opsForHash().put(botCommands,  "/user_type_xmr_amount", userTypeAmountXmrCommand);
            redisTemplate.opsForHash().put(botCommands,  "Сбербанк", userChooseSberbankCommand);
            redisTemplate.opsForHash().put(botCommands,  "/confirm_message", sendConfirmMessageCommand);
            redisTemplate.opsForHash().put(botCommands,  "/Согласен", newXmrExchangeOrderCommand);
            redisTemplate.opsForHash().put(botCommands,  "/Оплатил", userMadePaymentCommand);


            String botAdminCommands = "bot_admin_commands";
            redisTemplate.opsForHash().put(botAdminCommands, "/confirm", confirmPaymentAdminCommand);
            redisTemplate.opsForHash().put(botAdminCommands, "/complete", sendCoinsAdminCommand);
            log.info("Commands set complete.");
        } catch (TelegramApiException e) {
            log.error(e.getMessage());
        }

    }

}
