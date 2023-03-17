package com.klub.temporayStorageServer.app.configs.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaTopicConfig {

    @Bean
    public NewTopic fileDecomposition(){
        return TopicBuilder.name("file_decomposition")
                .build();
    }

    @Bean
    public NewTopic fileMetadata(){
        return TopicBuilder.name("file_metadata")
                .build();
    }
}
