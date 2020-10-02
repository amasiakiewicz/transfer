package com.casinoroyale.transfer.exchangerate.domain;

import static com.google.common.base.Preconditions.checkArgument;
import static java.lang.String.format;
import static lombok.AccessLevel.PACKAGE;

import java.math.BigDecimal;

import com.casinoroyale.exchangerate.exchangerate.dto.UpdateExchangeRateNoticeDto;
import lombok.AllArgsConstructor;
import org.joda.money.CurrencyUnit;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AllArgsConstructor(access = PACKAGE)
public class ExchangeRateFacade {

    private final ExchangeRateRepository exchangeRateRepository;

    public BigDecimal calculateConversionRate(final CurrencyUnit fromCurrency, final CurrencyUnit toCurrency) {
        checkArgument(fromCurrency != null);
        checkArgument(toCurrency != null);
        
        final ExchangeRate fromExchangeRate = findExchangeRate(fromCurrency);
        final ExchangeRate toExchangeRate = findExchangeRate(toCurrency);
        
        return toExchangeRate.calculateConversionRate(fromExchangeRate);
    }

    public void createOrUpdateExchangeRate(final CurrencyUnit currency, final UpdateExchangeRateNoticeDto updateExchangeRateNoticeDto) {
        checkArgument(currency != null);
        checkArgument(updateExchangeRateNoticeDto != null);

        final ExchangeRate exchangeRate = exchangeRateRepository
                .findByCurrency(currency)
                .orElseGet(() -> {
                    final ExchangeRate er = ExchangeRate.create(currency, updateExchangeRateNoticeDto);
                    return exchangeRateRepository.save(er);
                });

        exchangeRate.update(updateExchangeRateNoticeDto);
    }

    private ExchangeRate findExchangeRate(final CurrencyUnit currency) {
        return exchangeRateRepository
                .findByCurrency(currency)
                .<IllegalArgumentException>orElseThrow(() -> {
                    throw new IllegalArgumentException(format("Currency %s is not supported", currency));
                });
    }
}
