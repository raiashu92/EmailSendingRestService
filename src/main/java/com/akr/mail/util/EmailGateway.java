package com.akr.mail.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.logging.Logger;

@Component
public class EmailGateway {
    private static final Logger log = Logger.getLogger("EmailGateway.class");
    //use real for using gmail's gateway, otherwise it will default to printing on console
    private static final String WHICH_GATEWAY_TO_USE = "mock";

    @Autowired
    JavaMailSender javaMailSender;

    public boolean sendEmail(String toAddress, String subject, String mailText) {
        if (WHICH_GATEWAY_TO_USE.equals("real")) {
            log.info("Using real email gateway ...");
        } else {
            log.info("Using mock mail gateway ...");
            return MockMailGateway.sendEmail(toAddress, subject, mailText);
        }
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toAddress);
        msg.setSubject(subject);
        msg.setText(mailText);

        try {
            javaMailSender.send(msg);
            return true;
        } catch (MailException e) {
            return false;
        }
    }
}
