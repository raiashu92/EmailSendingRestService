package com.akr.mail.util;

import com.akr.mail.datastore.Order;
import com.akr.mail.model.SimpleMailObject;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;

//@Todo make async ??
@Component
public class EmailUtility {

    //use real for using gmail's gateway, otherwise it will default to printing on console
    private static final String WHICH_GATEWAY_TO_USE = "mock";
    private ConcurrentHashMap<Integer, String> emailState;
    private EmailGateway emailGateway;

    public EmailUtility() {
        this.emailState = new ConcurrentHashMap<>();
        if(WHICH_GATEWAY_TO_USE.equals("real"))
            this.emailGateway = new RealMailGateway();
        else
            this.emailGateway = new MockMailGateway();
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
            System.out.println("id: " + id + " emailstate size: " + emailState.size());
            emailState.put(id, "Sent");
        }
        else {
            System.out.println("id: " + id + " emailstate size: " + emailState.size());
            emailState.put(id, "Failed");
        }
    }

    public String getEmailStateForOrderId(int id) {
        return emailState.get(id);
    }

    public void setEmailStateForOrderId(int id, String state) {
        System.out.println("id: " + id + " emailstate size: " + emailState.size());
        emailState.put(id, state);
    }
}
