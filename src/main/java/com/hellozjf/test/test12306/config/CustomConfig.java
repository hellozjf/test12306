package com.hellozjf.test.test12306.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author hellozjf
 */
@Data
@Component
@ConfigurationProperties("custom")
public class CustomConfig {
    private String clientId;
    private String clientSecret;
    private String forder12306;
    private String netPrefix;
}
