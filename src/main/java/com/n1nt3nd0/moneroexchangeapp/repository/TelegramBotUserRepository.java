package com.n1nt3nd0.moneroexchangeapp.repository;


import com.n1nt3nd0.moneroexchangeapp.model.TelegramBotUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TelegramBotUserRepository extends JpaRepository<TelegramBotUser, Long> {
    Optional<TelegramBotUser> findTelegramBotUserByUsername(String username);
}
