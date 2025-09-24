package io.github.vibondarenko.clavionx.config;

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
    /**
     * Executor for handling asynchronous activity logging tasks
     * Configured with a core pool size of 2, max pool size of 5, and a queue capacity of 100
     *
     * @return configured Executor instance
     */
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