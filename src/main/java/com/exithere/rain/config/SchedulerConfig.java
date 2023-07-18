package com.exithere.rain.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class SchedulerConfig extends AsyncConfigurerSupport {

    private static final int POOL_SIZE = 256;

//    @Bean
//    public TaskScheduler taskScheduler(){
//        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
//        taskScheduler.setDaemon(true);
//        taskScheduler.setPoolSize(POOL_SIZE);
//        taskScheduler.setRemoveOnCancelPolicy(true);
//        taskScheduler.setThreadNamePrefix("Scheduler-");
//        taskScheduler.initialize();
//        return taskScheduler;
//    }

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(POOL_SIZE);
        executor.setQueueCapacity(50);
        executor.setThreadNamePrefix("TEST-ASYNC-");
        executor.initialize();
        return executor;
    }

}
