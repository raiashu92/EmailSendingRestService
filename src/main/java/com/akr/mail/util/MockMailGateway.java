package com.akr.mail.util;

import java.util.concurrent.atomic.AtomicInteger;

public class MockMailGateway implements EmailGateway {
    AtomicInteger count = new AtomicInteger(1);

    @Override
    public boolean sendEmail(String toAddress, String subject, String mailText) {
        //skipping every 3rd call to simulate mail sending failure
        if(count.get() == 3) {
            count.set(1);
            return false;
        }
        else {
            System.out.println("\n **************");
            System.out.println("Sending mail to: " + toAddress);
            System.out.println("Subject: " + subject);
            System.out.println("Mail content: \n" + mailText);
            System.out.println("\n **************");
            count.getAndIncrement();
            return true;
        }

    }
}
