package com.n1nt3nd0.moneroexchangeapp.repository;

import com.n1nt3nd0.moneroexchangeapp.model.BotUser;
import com.n1nt3nd0.moneroexchangeapp.model.XmrOrder;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface XmrExchangeOrderRepository extends JpaRepository<XmrOrder, Long> {

//    @EntityGraph(value = "xmr_order.bot_user")
    @Query(value = "select ord from XmrOrder ord where ord.botUser.username = :username ")
    Optional<XmrOrder> findXmrOrderByBotUser(@Param("username")String username);
}
