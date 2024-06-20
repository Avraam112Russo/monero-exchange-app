package com.n1nt3nd0.moneroexchangeapp.repository;


import com.n1nt3nd0.moneroexchangeapp.model.BotUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TelegramBotUserRepository extends JpaRepository<BotUser, Long> {
    Optional<BotUser> findTelegramBotUserByUsername(String username);
}
