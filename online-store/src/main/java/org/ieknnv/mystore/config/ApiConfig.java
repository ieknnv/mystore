package org.ieknnv.mystore.config;

import org.ieknnv.payment.client.api.BalanceApi;
import org.ieknnv.payment.client.api.PaymentApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApiConfig {

    @Bean
    public BalanceApi balanceApi() {
        return new BalanceApi();
    }

    @Bean
    public PaymentApi paymentApi() {
        return new PaymentApi();
    }
}
