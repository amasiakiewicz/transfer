package com.casinoroyale.transfer.exchangerate.domain;

import static com.casinoroyale.transfer.TransferApplication.DEFAULT_ZONE_OFFSET;
import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;

import java.math.BigDecimal;

import com.casinoroyale.exchangerate.exchangerate.dto.UpdateExchangeRateNoticeDto;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.Test;

class ExchangeRateTest {

    @Test
    void shouldCalculateConversionRate() {
        //given
        final ExchangeRate fromExchangeRate = givenExchangeRate(CurrencyUnit.of("EUR"), 1.0);
        final ExchangeRate toExchangeRate = givenExchangeRate(CurrencyUnit.of("PLN"), 4.5557);

        //when
        final BigDecimal conversionRate = toExchangeRate.calculateConversionRate(fromExchangeRate);

        //then
        assertThat(conversionRate).isEqualByComparingTo("4.5557");
    }

    private ExchangeRate givenExchangeRate(final CurrencyUnit currency, final double rate) {
        final UpdateExchangeRateNoticeDto rateDto = new UpdateExchangeRateNoticeDto(BigDecimal.valueOf(rate), now(DEFAULT_ZONE_OFFSET));
        return ExchangeRate.create(currency, rateDto);
    }
}