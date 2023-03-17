package com.klub.temporayStorageServer.app.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfigsBeans {


    @Bean
    public ObjectMapper defaultMapper() {
        return new ObjectMapper();
    }
}
