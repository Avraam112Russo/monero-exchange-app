package com.n1nt3nd0.moneroexchangeapp.service.bot.botCommands;

import com.jayway.jsonpath.JsonPath;
import com.n1nt3nd0.moneroexchangeapp.dao.DaoBotState;
import com.n1nt3nd0.moneroexchangeapp.model.BotLastState;
import com.n1nt3nd0.moneroexchangeapp.model.BotUser;
import com.n1nt3nd0.moneroexchangeapp.model.bot_last_state.BotStateEnum;
import com.n1nt3nd0.moneroexchangeapp.repository.TelegramBotUserRepository;
import com.n1nt3nd0.moneroexchangeapp.service.BlockChairApi;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserTypeAmountXmrCommand implements BotCommand {
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
        userTypeAmountXmrCommand(telegramClient, daoBotState, telegramBotUserRepository, chatId, text, username, restTemplate);
    }

    private void userTypeAmountXmrCommand(TelegramClient telegramClient,
                              DaoBotState daoBotState,
                              TelegramBotUserRepository telegramBotUserRepository,
                              Long chatId,
                              String text,
                              String username,
                              RestTemplate restTemplate

    ) {
        daoBotState.saveAmountXmrUserWantBuy(chatId.toString(), Double.parseDouble(text), username);
        UriComponentsBuilder uriComponentsBuilder =
                UriComponentsBuilder.fromHttpUrl("https://api.blockchair.com/monero/stats");
        Object response = restTemplate.getForObject(uriComponentsBuilder.toUriString(), Object.class);
        String lastXmrMarketPrice = JsonPath.parse(response).read("$.context.market_price_usd", String.class);
        log.info(lastXmrMarketPrice);
        daoBotState.saveCurrentlyXmrMarketPrice(lastXmrMarketPrice, chatId.toString());
        double marketPriceRub = Double.parseDouble(lastXmrMarketPrice) * 92; // 15.970, 283729378293
        Double truncatedMarketPriceRub = BigDecimal.valueOf(marketPriceRub).setScale(3, RoundingMode.HALF_UP).doubleValue(); // 15.970, 283
        Double checkOutSum = Double.parseDouble(text) * marketPriceRub;
        String messageText = "Средний рыночный курс XMR: " + lastXmrMarketPrice + " USD, " + truncatedMarketPriceRub + " RUB\n" +
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
            daoBotState.updateCurrentlyBotState(
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
}
