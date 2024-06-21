package com.n1nt3nd0.moneroexchangeapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "t_bot_users")
@Entity
@Builder
public class BotUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "user_name")
    private String username;
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    @Column(name = "chat_id")
    private Long chatId;
}
