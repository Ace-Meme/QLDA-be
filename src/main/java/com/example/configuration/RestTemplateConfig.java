package com.example.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.BufferingClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

@Configuration
public class RestTemplateConfig {

    private static final Logger logger = LoggerFactory.getLogger(RestTemplateConfig.class);

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate(
            new BufferingClientHttpRequestFactory(
                new SimpleClientHttpRequestFactory()
            )
        );
        
        restTemplate.setInterceptors(
            Collections.singletonList(new LoggingInterceptor())
        );
        
        return restTemplate;
    }
    
    static class LoggingInterceptor implements ClientHttpRequestInterceptor {
        @Override
        public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
            logger.debug("Request: {} {}", request.getMethod(), request.getURI());
            logger.debug("Headers: {}", request.getHeaders());
            logger.debug("Request body: {}", new String(body, StandardCharsets.UTF_8));
            
            ClientHttpResponse response = execution.execute(request, body);
            
            return response;
        }
    }
} 