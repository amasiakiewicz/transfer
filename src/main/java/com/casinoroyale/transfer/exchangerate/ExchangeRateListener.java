package com.casinoroyale.transfer.exchangerate;

import static com.google.common.base.Preconditions.checkArgument;

import com.casinoroyale.exchangerate.exchangerate.dto.UpdateExchangeRateNoticeDto;
import com.casinoroyale.transfer.exchangerate.domain.ExchangeRateFacade;
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
    public void listen(ConsumerRecord<String, UpdateExchangeRateNoticeDto> kafkaMessage) {
        final String currencyStr = kafkaMessage.key();
        checkArgument(currencyStr != null);

        final CurrencyUnit currency = CurrencyUnit.of(currencyStr);
        final UpdateExchangeRateNoticeDto updateExchangeRateNoticeDto = kafkaMessage.value();

        exchangeRateFacade.createOrUpdateExchangeRate(currency, updateExchangeRateNoticeDto);
    }
    
}
