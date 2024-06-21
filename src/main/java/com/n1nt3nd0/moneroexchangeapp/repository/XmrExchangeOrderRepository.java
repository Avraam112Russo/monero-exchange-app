package com.n1nt3nd0.moneroexchangeapp.repository;

import com.n1nt3nd0.moneroexchangeapp.model.XmrOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface XmrExchangeOrderRepository extends JpaRepository<XmrOrder, Long> {
}
