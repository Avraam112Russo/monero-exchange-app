package com.n1nt3nd0.moneroexchangeapp.redis;

import io.lettuce.core.RedisClient;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;
import jakarta.annotation.PostConstruct;
import lombok.Cleanup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

@Component
@Slf4j
public class RedisStorageTelegramBot {

    @PostConstruct
    public void init() {
      log.info("Redis Storage Telegram Bot init..");
    }
    public void saveUser(String username, String firstName){
        RedisClient redisClient;
        StatefulRedisConnection<String, String> redisConnection;
        RedisCommands<String, String> syncCommands;

        redisClient = RedisClient.create("redis://localhost:6379/0"); // Format: redis://ip:post/dbNumber
        redisConnection = redisClient.connect();
        syncCommands = redisConnection.sync();

        syncCommands.set(username, firstName);
        log.info(username + "successfully stored in redis");
//        syncCommands.lrange("key", 0, -1);

//        syncCommands.get("key");

        String response = syncCommands.get(username);
        log.info(response);
        redisConnection.close();
        redisClient.shutdown();
    }
}

