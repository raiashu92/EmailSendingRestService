package com.akr.mail.util;

import com.akr.mail.datastore.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.logging.Logger;

@Component
@Scope("prototype")
public class RequestExecutor implements Runnable {
    private static final Logger log = Logger.getLogger("RequestExecutor.class");
    private Integer id;
    private Order order;

    @Autowired
    EmailGateway emailGateway;
    @Autowired
    EmailUtility emailUtility;

    public void setParameters(Integer id, Order order) {
        this.id = id;
        this.order = order;
    }

    @Override
    public void run() {
        log.info("Sending mail in thread: " + Thread.currentThread().getName());

        String subject = "Update for your order id: " + id;
        StringBuffer mailBody = new StringBuffer("Dear " + order.getCustomerName() + "\n");
        if ("cancelled".equalsIgnoreCase(order.getOrderStatus()))
            mailBody.append("\n  Your order has been " + order.getOrderStatus() + ", reason: " + order.getCancelReason() + "\n");
        else
            mailBody.append("\n  Your order has been " + order.getOrderStatus() + "\n");
        mailBody.append("  Please contact customer support for any issues" + "\n");
        mailBody.append("\nThanking you \nMart.com");

        if (emailGateway.sendEmail(order.getEmail(), subject, mailBody.toString())) {
            emailUtility.setEmailStateForOrderId(id, "Sent");
        } else {
            emailUtility.setEmailStateForOrderId(id, "Failed");
        }
    }
}
