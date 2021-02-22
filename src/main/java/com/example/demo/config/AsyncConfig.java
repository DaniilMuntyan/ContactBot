package com.example.demo.config;

import com.example.demo.constants.ProgramVariables;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

@Configuration
public class AsyncConfig {
    private static final Logger LOGGER = Logger.getLogger(AsyncConfig.class);

    private final ProgramVariables programVariables;

    @Autowired
    public AsyncConfig(ProgramVariables programVariables) {
        LOGGER.info("AsyncConfig is creating...");
        this.programVariables = programVariables;
    }

    @Bean
    public TaskExecutor getAsyncExecutor() {
        ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
        int corePoolSize = Integer.parseInt(programVariables.getThreadsCorePoolSize());
        int maxPoolSize = Integer.parseInt(programVariables.getThreadsMaxPoolSize());
        int queueCapacity = Integer.parseInt(programVariables.getThreadsQueueCapacity());
        taskExecutor.setCorePoolSize(corePoolSize);
        taskExecutor.setMaxPoolSize(maxPoolSize);
        taskExecutor.setQueueCapacity(queueCapacity);
        taskExecutor.setThreadNamePrefix("user-thread-");
        LOGGER.info("Created TaskExecutor bean. CorePoolSize: " + corePoolSize + ". " +
                "MaxPoolSize: " + maxPoolSize + ". " + "QueueCapacity: " + queueCapacity);
        return taskExecutor;
    }
}
