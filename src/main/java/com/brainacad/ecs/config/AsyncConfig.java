package com.brainacad.ecs.config;

import java.util.concurrent.Executor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

/**
 * Configuration for asynchronous processing
 * Enables async activity logging and other background tasks
 */
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "activityLoggerExecutor")
    public Executor activityLoggerExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("ActivityLogger-");
        executor.initialize();
        return executor;
    }
}
