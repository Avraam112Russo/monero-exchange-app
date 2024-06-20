package com.n1nt3nd0.moneroexchangeapp.dao;

import com.n1nt3nd0.moneroexchangeapp.model.BotLastState;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class DaoBotState {


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
    public BotLastState getCurrentlyBotState(String chatID){
        return (BotLastState) redisTemplate.opsForHash().get("bot_state", chatID);
    }
    public void saveAmountXmrUserWantBuy(String chatID, Double amountXmrUserWantBuy, String username){
        String key = "amount_xmr_user_want_buy";
        redisTemplate.opsForHash().put(key, chatID, amountXmrUserWantBuy);
        log.info("New record saved in {} \n ChatID: {}, Amount: {}, Username: {}",key, chatID, amountXmrUserWantBuy, username);
    }
}

