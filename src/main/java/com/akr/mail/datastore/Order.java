package com.akr.mail.datastore;

import com.akr.mail.model.NewOrderObject;
import org.springframework.data.annotation.Persistent;

import javax.persistence.*;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Entity
@Table(name = "order_table")
public class Order {

    //@ToDo add column specific data integrity checks, add auto time generator in constructor

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int orderId;

    @Column(nullable = false)
    private String itemName;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String orderTime;

    @Column(nullable = false)
    private String email;

    private String orderStatus;
    private String cancelTime;
    private String shipTime;
    private String cancelReason;

    public Order() {
    }

    public Order(String itemName, String customerName, String email) {
        this.itemName = itemName;
        this.customerName = customerName;
        this.orderTime = LocalTime.now().format(DateTimeFormatter.ofPattern("h:mm:ss a"));
        this.email = email;
        this.orderStatus = "received";
    }

    public Order(NewOrderObject orderObject) {
        this(orderObject.getItemName(), orderObject.getCustomerName(), orderObject.getEmail());
    }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", itemName='" + itemName + '\'' +
                ", customerName='" + customerName + '\'' +
                ", orderTime='" + orderTime + '\'' +
                ", email='" + email + '\'' +
                ", orderStatus='" + orderStatus + '\'' +
                ", cancelTime='" + cancelTime + '\'' +
                ", shipTime='" + shipTime + '\'' +
                ", cancelReason='" + cancelReason + '\'' +
                '}';
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(String orderTime) {
        this.orderTime = orderTime;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCancelTime() {
        return cancelTime;
    }

    public void setCancelTime(String cancelTime) {
        this.cancelTime = cancelTime;
    }

    public String getShipTime() {
        return shipTime;
    }

    public void setShipTime(String shipTime) {
        this.shipTime = shipTime;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public void setCancelReason(String cancelReason) {
        this.cancelReason = cancelReason;
    }
}
