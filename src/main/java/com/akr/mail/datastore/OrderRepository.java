package com.akr.mail.datastore;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface OrderRepository extends CrudRepository<Order, Integer> {
    //additional query methods
    List<Order> findByCustomerNameIgnoreCase(String customerName);
    List<Order> findByEmailIgnoreCase(String email);
}
