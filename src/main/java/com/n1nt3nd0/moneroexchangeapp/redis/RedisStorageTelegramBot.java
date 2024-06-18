package com.n1nt3nd0.moneroexchangeapp.redis;

import com.n1nt3nd0.moneroexchangeapp.model.XmrOrder;
import com.n1nt3nd0.moneroexchangeapp.repository.XmrOrderRepository;
import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import io.lettuce.core.api.sync.RedisKeyCommands;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.Cleanup;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Component
@Slf4j
@RequiredArgsConstructor
public class RedisStorageTelegramBot {

    private final XmrOrderRepository xmrOrderRepository;
    @PostConstruct
    public void init() {
      log.info("Redis Storage Telegram Bot init..");
    }
    @PreDestroy
    public void destroy() {
        log.info("Redis Storage Telegram Bot destroy.");
    }
    public void saveXmrOrder(String username, Double amount){
        XmrOrder build = XmrOrder.builder()
                .id(UUID.randomUUID().toString())
                .startDate(LocalDateTime.now())
                .address("default")
                .amount(amount)
                .status(true)
                .username(username)
                .build();
        xmrOrderRepository.save(build);
        log.info("Saved XmrOrder: {}", build);
    }

    public XmrOrder createNewXmrOrder(String username){
//        RedisClient redisClient;
//        StatefulRedisConnection<String, String> redisConnection;
//        RedisCommands<String, String> syncCommands;




return null;
    }

    public void saveAmountXmrOrder(String username, String amount){



    }
}

