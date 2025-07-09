package com.cardcostapi.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "ratelimit")
@Getter
@Setter
public class InMemoryRateLimitConfig {

    private int limit;

    private int duration;

}
