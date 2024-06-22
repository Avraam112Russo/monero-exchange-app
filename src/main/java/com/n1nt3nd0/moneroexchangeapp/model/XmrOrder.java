package com.n1nt3nd0.moneroexchangeapp.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
//
//@NamedEntityGraph(name = "xmr_order.bot_user",
//        attributeNodes = {
//                @NamedAttributeNode(value = "botUser"),
//        }
//)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "t_xmr_order")
public class XmrOrder implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "xmr_quantity")
    private Double quantity;
    @Column(name = "price_in_ruble")
    private Double priceInRuble;
    @Column(name = "payment_method")
    @Enumerated(EnumType.STRING)
    private PaymentMethod payMentMethod;
    @Column(name = "address")
    private String address;
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private BotUser botUser;

}
