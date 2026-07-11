package com.db.payment.config;

import feign.Logger;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

public class FeignConfig {


    @Bean("payFeignClient")
    @Primary// ← makes this the preferred bean
    public CloseableHttpClient feignClient() {
        return HttpClients.createDefault();
    }

    @Bean
    Logger.Level feignLoggerLevel() {
        return Logger.Level.FULL;
    }
}
