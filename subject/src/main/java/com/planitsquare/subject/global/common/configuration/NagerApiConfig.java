package com.planitsquare.subject.global.common.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class NagerApiConfig {
    @Bean
    public WebClient nagerWebClient(
            @Value("${nager.api.base-url}") String baseUrl
    ) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}
