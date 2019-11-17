package com.akr.mail.util;

import com.akr.mail.datastore.Order;
import com.akr.mail.model.SimpleMailObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

@Component
public class EmailUtility {
    private static final Logger log = Logger.getLogger("EmailUtility.class");
    private ConcurrentHashMap<Integer, String> emailState;

    @Autowired
    EmailGateway emailGateway;
    @Autowired
    TaskExecutor taskExecutor;
    @Autowired
    ApplicationContext applicationContext;

    public EmailUtility() {
        this.emailState = new ConcurrentHashMap<>();
    }

    public boolean sendSimpleEmail(SimpleMailObject simpleMailObject) {
        return emailGateway.sendEmail(simpleMailObject.getToAddress(), simpleMailObject.getSubject(), simpleMailObject.getMailBody());
    }

    public void sendTemplateEmail(int id, Order order) {
        log.info("Delegating send mail task to executor");
        RequestExecutor request = applicationContext.getBean(RequestExecutor.class);
        request.setParameters(id, order);
        taskExecutor.execute(request);
    }

    public String getEmailStateForOrderId(int id) {
        return emailState.get(id);
    }

    public void setEmailStateForOrderId(int id, String state) {
        emailState.put(id, state);
    }
}
