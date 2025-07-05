package com.fitai.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "ai.api")
@Data
public class AIConfig {
    private String key;
    private String baseUrl;
    private String model;
    private Integer timeout;
    private Integer maxTokens;
}