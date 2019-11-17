package com.akr.mail;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@SpringBootApplication
@EnableAsync
public class StarterMain {

    public static void main(String[] args) {
        SpringApplication.run(StarterMain.class, args);
    }

    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor threadExec = new ThreadPoolTaskExecutor();
        threadExec.setCorePoolSize(2);
        threadExec.setMaxPoolSize(4);
        threadExec.setThreadNamePrefix("EmailREST-");
        threadExec.setQueueCapacity(200);
        threadExec.initialize();
        return threadExec;
    }

}
