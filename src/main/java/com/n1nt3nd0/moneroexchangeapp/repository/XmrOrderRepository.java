package com.n1nt3nd0.moneroexchangeapp.repository;

import com.n1nt3nd0.moneroexchangeapp.model.XmrOrder;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface XmrOrderRepository extends CrudRepository<XmrOrder, String> {
}
