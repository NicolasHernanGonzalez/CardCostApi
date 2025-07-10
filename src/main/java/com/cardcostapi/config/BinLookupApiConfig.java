package com.cardcostapi.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@ConfigurationProperties(prefix = "binlookup.api")
@Getter
@Setter
public class BinLookupApiConfig {
    private String baseUrl;
    private int connectTimeout;
    private int readTimeout;
    private int retries;
}
