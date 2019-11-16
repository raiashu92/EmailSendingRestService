package com.akr.mail.service;

import com.akr.mail.datastore.Order;
import com.akr.mail.datastore.OrderRepository;
import com.akr.mail.model.NewOrderObject;
import com.akr.mail.model.StatusUpdate;
import com.akr.mail.util.EmailUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    OrderRepository orderRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    EmailUtility emailUtility;

    public int add(NewOrderObject orderObject) {
        Order order = new Order(orderObject);
        int id = orderRepository.save(order).getOrderId();
        emailUtility.setEmailStateForOrderId(id, "Sending");
        emailUtility.sendTemplateEmail(id, order);
        return id;
    }

    public List<Order> findOrdersByEmail(String mail) {
        return orderRepository.findByEmailIgnoreCase(mail);
    }

    public List<Order> findOrdersByCustomerName(String name) {
        return orderRepository.findByCustomerNameIgnoreCase(name);
    }

    public String delete(int id) {
        try {
            orderRepository.deleteById(id);
            return "Order with " + id + " has been deleted!";
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public Optional<Order> findOrderById(int id) {
        return orderRepository.findById(id);
    }

    public List<Order> getAllOrders() {
        return (List<Order>) orderRepository.findAll();
    }

    public String getOrderStatus(int id) {
        Optional<Order> order = orderRepository.findById(id);
        if(order.isPresent())
            return order.get().getOrderStatus();
        else
            return null;
    }

    @Transactional
    public String changeOrderState(int id, StatusUpdate statusUpdate) {
        Order order = findOrderById(id).get();
        String currentStatus = statusUpdate.getStatus();
        String orderStatus = order.getOrderStatus();

        if ("cancelled".equalsIgnoreCase(orderStatus) || "delivered".equalsIgnoreCase(orderStatus)
                || currentStatus.equalsIgnoreCase(orderStatus)) {
            return ("order with id " + id + " already " + orderStatus);
        }
        order.setOrderStatus(currentStatus);
        if (currentStatus.equalsIgnoreCase("shipped")) {
            order.setShipTime(LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm:ss a")));
        } else if (currentStatus.equalsIgnoreCase("cancelled")) {
            order.setCancelTime(LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm:ss a")));
            order.setCancelReason(statusUpdate.getReason());
        } else {
            return "BAD_REQUEST";
        }

        updateOrder(order);
        emailUtility.setEmailStateForOrderId(id, "Sending");
        emailUtility.sendTemplateEmail(id, order);
        return ("order with id " + id + " is " + currentStatus +
                ". An email with update will be sent.");
    }

    @Transactional
    public void updateOrder(Order order) {
        entityManager.persist(order);
    }
}
