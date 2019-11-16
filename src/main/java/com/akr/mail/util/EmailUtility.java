package com.akr.mail.util;

import com.akr.mail.datastore.Order;
import com.akr.mail.model.SimpleMailObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

//@Todo make async ??
@Component
public class EmailUtility {
    private static final Logger log = Logger.getLogger("EmailUtility.class");
    private ConcurrentHashMap<Integer, String> emailState;
    @Autowired
    EmailGateway emailGateway;

    public EmailUtility() {
        this.emailState = new ConcurrentHashMap<>();
    }

    public boolean sendSimpleEmail(SimpleMailObject simpleMailObject) {
        return emailGateway.sendEmail(simpleMailObject.getToAddress(), simpleMailObject.getSubject(), simpleMailObject.getMailBody());
    }

    public void sendTemplateEmail(int id, Order order) {
        String subject = "Update for your order id: " + id;
        StringBuffer mailBody = new StringBuffer("Dear " + order.getCustomerName() + "\n");
        if ("cancelled".equalsIgnoreCase(order.getOrderStatus()))
            mailBody.append("\n  Your order has been " + order.getOrderStatus() + ", reason: " + order.getCancelReason() + "\n");
        else
            mailBody.append("\n  Your order has been " + order.getOrderStatus() + "\n");
        mailBody.append("  Please contact customer support for any issues" + "\n");
        mailBody.append("\nThanking you \nMart.com");

        if (emailGateway.sendEmail(order.getEmail(), subject, mailBody.toString())) {
            emailState.put(id, "Sent");
        } else {
            emailState.put(id, "Failed");
        }
    }

    public String getEmailStateForOrderId(int id) {
        return emailState.get(id);
    }

    public void setEmailStateForOrderId(int id, String state) {
        emailState.put(id, state);
    }
}
