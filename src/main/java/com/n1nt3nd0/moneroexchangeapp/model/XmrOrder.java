package com.n1nt3nd0.moneroexchangeapp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@RedisHash("XmlOrder")
public class XmrOrder implements Serializable {
    private String id;
    private LocalDateTime startDate;
    private String username;
    private Double amount;
    private String address;
    private Boolean status;
}
