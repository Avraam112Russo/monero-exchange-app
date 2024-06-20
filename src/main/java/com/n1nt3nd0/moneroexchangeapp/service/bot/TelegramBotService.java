package com.n1nt3nd0.moneroexchangeapp.service.bot;

import com.n1nt3nd0.moneroexchangeapp.dao.DaoBotState;
import com.n1nt3nd0.moneroexchangeapp.model.bot_last_state.BotStateEnum;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.longpolling.util.LongPollingSingleThreadUpdateConsumer;
import org.telegram.telegrambots.meta.api.objects.Update;

@Service
@RequiredArgsConstructor
@Slf4j
public class TelegramBotService implements LongPollingSingleThreadUpdateConsumer {

    private final BotCommandHandler botCommandHandler;
    private final BotCallBackQueryHandler botCallBackQueryHandler;
    private final RestTemplate restTemplate;
    private final DaoBotState daoBotState;
    @SneakyThrows
    @Override
    public void consume(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String text = update.getMessage().getText();
            String username = update.getMessage().getChat().getUserName();
            Long chatId = update.getMessage().getChatId();
            switch (text){
                case "/start" :
                    botCommandHandler.startCommand(chatId, text, username);
                        break;
                        case "/state" :
                        botCommandHandler.stateCommandHandle(chatId.toString());
                        break;


            }

            if (text.contains(".") && daoBotState.getCurrentlyBotState(String.valueOf(chatId)).getLastBotStateEnum().name().equals(BotStateEnum.BUY_XMR_COMMAND.name())){
                botCommandHandler.userTypeXmrAmountWantBuy(text, chatId, username);
            }
                // 0.5 xmr and last state"Buy Xmr"
        }
        else if (update.hasCallbackQuery()) {
            String call_data = update.getCallbackQuery().getData();
            long message_id = update.getCallbackQuery().getMessage().getMessageId();
            long chat_id = update.getCallbackQuery().getMessage().getChatId();
            String username = update.getCallbackQuery().getMessage().getChat().getUserName();
            botCallBackQueryHandler.callBackQueryHandle(call_data, message_id, chat_id, username);
        }
    }

    }
