package com.akr.mail.util;

public interface EmailGateway {
    boolean sendEmail(String toAddress, String subject, String mailText);
}
