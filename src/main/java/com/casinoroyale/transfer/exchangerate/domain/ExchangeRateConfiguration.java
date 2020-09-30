package com.casinoroyale.transfer.exchangerate.domain;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
class ExchangeRateConfiguration {
    
    @Bean
    ExchangeRateFacade exchangeRateFacade(final ExchangeRateRepository exchangeRateRepository) {
        return new ExchangeRateFacade(exchangeRateRepository);
    }

}
