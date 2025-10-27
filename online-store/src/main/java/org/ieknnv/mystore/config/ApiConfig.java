package org.ieknnv.mystore.config;

import org.ieknnv.payment.client.api.BalanceApi;
import org.ieknnv.payment.client.api.PaymentApi;
import org.ieknnv.payment.client.invoker.ApiClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfig {

    @Value("${payment.server.url}")
    private String paymentServerUrl;

    @Bean
    public BalanceApi balanceApi() {
        var apiClient = new ApiClient();
        apiClient.setBasePath(paymentServerUrl);
        return new BalanceApi(apiClient);
    }

    @Bean
    public PaymentApi paymentApi() {
        var apiClient = new ApiClient();
        apiClient.setBasePath(paymentServerUrl);
        return new PaymentApi(apiClient);
    }
}
