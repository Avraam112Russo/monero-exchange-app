package com.n1nt3nd0.moneroexchangeapp.service.bot;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;
import com.n1nt3nd0.moneroexchangeapp.redis.RedisStorageTelegramBot;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.chat.Chat;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.TelegramClient;

import java.awt.geom.Arc2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import static java.lang.Math.toIntExact;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramBot implements LongPollingSingleThreadUpdateConsumer {

    private final BotCommandHandler botCommandHandler;
    private final BotCallBackQueryHandler botCallBackQueryHandler;
    private final RestTemplate restTemplate;
    @SneakyThrows
    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            String username = update.getMessage().getChat().getUserName();
            String firstName = update.getMessage().getChat().getFirstName();
            String lastName = update.getMessage().getChat().getLastName();

            Long chatId = update.getMessage().getChatId();
            if (text.equals("/start")){
                    botCommandHandler.startCommand(chatId, text, username, firstName, lastName);
            }
            else if (text.contains(".")){
                if (Double.parseDouble(text) > 0.15)
               try {
                   log.info("Пользователь запросил %s xmr.".formatted(text));
                   UriComponentsBuilder uriComponentsBuilder =
                           UriComponentsBuilder.fromHttpUrl("https://api.blockchair.com/monero/stats");
                   Object response = restTemplate.getForObject(uriComponentsBuilder.toUriString(), Object.class);
                   log.info(response.toString());

                   String lastXmrMarketPrice = JsonPath.parse(response).read("$.context.market_price_usd", String.class);
                   log.info(lastXmrMarketPrice.toString());

                   Double orderPriceUsd = Double.parseDouble(text) * Double.parseDouble(lastXmrMarketPrice);
                   Double orderPriceRub = 92 * orderPriceUsd;
                   botCommandHandler.completeCheckoutCommand(chatId, username, Double.parseDouble(text), lastXmrMarketPrice);
               }catch (Exception exception){
                   log.error("Error while text.contains(\".\")"+exception.getMessage());
               }

            }
            else if (text.length() > 50){
                String message = "Время на оплату заказа 15 минут!\n" +
                        "\n" +
                        "Средства отправленные без заявки, возврату НЕ подлежат! Оплачивать вы должны ровно ту сумму, которая указана в заявке, иначе мы ваш платеж не найдем!  Все претензии по обмену принимаются в течении 24 часов. \n" +
                        "Обращаем внимание: средства вы должны отправлять только со своей личной карты. Администрация может потребовать верификацию документов клиента или задержать обмен для проверки других данных.\n" +
                        "\n" +
                        "Итого к оплате: 63 208 руб.\n" +
                        "\n" +
                        "ВНИМАТЕЛЬНО сверяйте адрес своего кошелька!\n" +
                        "\n" +
                        "После оплаты средства будут переведены на кошелек: bc1qfw0a6xhxp9hn6emuxu6sjhw9pkyus2vmf0gl99\n" +
                        "\n" +
                        " \n" +
                        "\n" +
                        "Если у вас есть вопрос, или возникли проблемы с оплатой, пишите поддержке: @MONOPOLYBANKER\n" +
                        "\n" +
                        "Вы согласны на обмен?";
            }
        }

        else if (update.hasCallbackQuery()){
            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();
            String username = update.getCallbackQuery().getMessage().getChat().getUserName();
            botCallBackQueryHandler.callBackQueryHandle(call_data, message_id, chat_id, username);
        }
    }


//    }
}
