package com.akr.mail.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

public class RealMailGateway implements EmailGateway {

    @Autowired
    private JavaMailSender javaMailSender;

    @Override
    public boolean sendEmail(String toAddress, String subject, String mailText) {
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
