package com.n1nt3nd0.moneroexchangeapp.model;

import com.n1nt3nd0.moneroexchangeapp.model.bot_last_state.BotStateEnum;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@RedisHash("BotLastState")
public class BotLastState implements Serializable {
    private String id;
    private String chatId;
    private BotStateEnum lastBotStateEnum;
}
