package com.n1nt3nd0.moneroexchangeapp.dao;

import com.n1nt3nd0.moneroexchangeapp.model.BotLastState;
import com.n1nt3nd0.moneroexchangeapp.model.XmrOrder;
import com.n1nt3nd0.moneroexchangeapp.model.bot_last_state.BotStateEnum;
import com.n1nt3nd0.moneroexchangeapp.repository.XmrExchangeOrderRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Optional;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class DaoBotState implements Serializable {


    private final XmrExchangeOrderRepository xmrExchangeOrderRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    @PostConstruct
    public void init() {
      log.info("Redis Storage Telegram Bot init..");
    }
    @PreDestroy
    public void destroy() {
        log.info("Redis Storage Telegram Bot destroy.");
    }

    public void  updateCurrentlyBotState(BotLastState botLastState){
        redisTemplate.opsForHash().put("bot_state", botLastState.getChatId(), botLastState);
        log.info("User {} update bot state: {}", botLastState.getChatId(), botLastState.getLastBotStateEnum().name());
    }
    public BotLastState getCurrentlyBotState(String chatId){
        BotLastState botState = (BotLastState) redisTemplate.opsForHash().get("bot_state", chatId);
        if (botState == null){
            return BotLastState.builder()
                    .chatId(String.valueOf(chatId))
                    .lastBotStateEnum(BotStateEnum.START_COMMAND)
                    .id(UUID.randomUUID().toString())
                    .build();
        }
        return botState;
    }
    public void saveAmountXmrUserWantBuy(String chatID, Double amountXmrUserWantBuy, String username){
        String key = "amount_xmr_user_want_buy";
        redisTemplate.opsForHash().put(key, chatID, amountXmrUserWantBuy);
        log.info("New record saved in {} \n ChatID: {}, Amount: {}, Username: {}",key, chatID, amountXmrUserWantBuy, username);
    }
    public Double getAmountXmrUserWantBuy(String chatID){
        String key = "amount_xmr_user_want_buy";
        return (Double) redisTemplate.opsForHash().get(key, chatID);
    }
    public void saveUserXmrAddress(String address, String chatID){
        String key = "user_xmr_address";
        redisTemplate.opsForHash().put(key, chatID, address);
        log.info("User xmr address {} saved successfully", address);
    }
    public String getUserXmrAddress(String chatID){
        String key = "user_xmr_address";
        return (String) redisTemplate.opsForHash().get(key, chatID);
    }
    public void savePaymentMethod(String chatId, String paymentMethod){
        String key = "payment_method";
        redisTemplate.opsForHash().put(key, chatId, paymentMethod);
        log.info("User payment method {} saved successfully", paymentMethod);
    }
    public String getPaymentMethod(String chatID){
        return (String) redisTemplate.opsForHash().get("payment_method", chatID);
    }

    public void saveCurrentlyXmrMarketPrice(String currentlyXmrMarketPrice, String chatID){
        String key = "currentlyXmrMarketPrice";
        redisTemplate.opsForHash().put(key, chatID, currentlyXmrMarketPrice);
    }

    public double getCurrentlyXmrMarketPrice(String chatID){
        String key = "currentlyXmrMarketPrice";
        String price =  (String) redisTemplate.opsForHash().get(key, chatID);
        double convertedPrice = Double.parseDouble(price);
        return convertedPrice;
    }
    public void saveNewXmrExchangeOrder(XmrOrder xmrOrder){
        xmrExchangeOrderRepository.save(xmrOrder);
        log.info("Order {} saved successfully in db", xmrOrder);
    }
    public XmrOrder getNewXmrExchangeOrder(String username){
        Optional<XmrOrder> xmrOrderByBotUser = xmrExchangeOrderRepository.findXmrOrderByBotUser(username);
        if (xmrOrderByBotUser.isPresent()){
            return xmrOrderByBotUser.get();
        }
        throw new RuntimeException("Xmr order not found");
    }
    public void deleteOrderIfUserAlreadyHaveIt(String username){
        Optional<XmrOrder> xmrOrderByBotUser = xmrExchangeOrderRepository.findXmrOrderByBotUser(username);
        if (xmrOrderByBotUser.isPresent()){
            xmrExchangeOrderRepository.delete(xmrOrderByBotUser.get());
        }
    }

    public void removeXmrOrder(String username) {
        Optional<XmrOrder> xmrOrderByBotUser = xmrExchangeOrderRepository.findXmrOrderByBotUser(username);
        if (xmrOrderByBotUser.isPresent()){
            xmrExchangeOrderRepository.delete(xmrOrderByBotUser.get());
        }
    }
}

