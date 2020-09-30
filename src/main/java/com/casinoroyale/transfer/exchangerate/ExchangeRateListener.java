package com.casinoroyale.transfer.exchangerate;

import com.casinoroyale.transfer.exchangerate.domain.ExchangeRateFacade;
import com.casinoroyale.transfer.exchangerate.dto.UpdateExchangeRateDto;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.joda.money.CurrencyUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
class ExchangeRateListener {
    
    private final ExchangeRateFacade exchangeRateFacade;

    @Autowired
    ExchangeRateListener(final ExchangeRateFacade exchangeRateFacade) {
        this.exchangeRateFacade = exchangeRateFacade;
    }

    @KafkaListener(topics = "ExchangeRateUpdated")
    public void listen(ConsumerRecord<CurrencyUnit, UpdateExchangeRateDto> kafkaMessage) {
        final CurrencyUnit currency = kafkaMessage.key();
        final UpdateExchangeRateDto updateExchangeRateDto = kafkaMessage.value();
        
        exchangeRateFacade.createOrUpdateExchangeRate(currency, updateExchangeRateDto);
    }
    
}
