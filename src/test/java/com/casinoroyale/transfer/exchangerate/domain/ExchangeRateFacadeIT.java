package com.casinoroyale.transfer.exchangerate.domain;

import static com.casinoroyale.transfer.TransferApplication.DEFAULT_ZONE_OFFSET;
import static java.math.BigDecimal.valueOf;
import static java.time.OffsetDateTime.now;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.ThrowableAssert.catchThrowable;

import java.math.BigDecimal;
import java.util.Optional;

import com.casinoroyale.exchangerate.exchangerate.dto.UpdateExchangeRateNoticeDto;
import org.joda.money.CurrencyUnit;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ExchangeRateFacadeIT {

    @Autowired //SUT
    private ExchangeRateFacade exchangeRateFacade;
    
    @Autowired
    private ExchangeRateRepository exchangeRateRepository;
    
    @Test
    void shouldCalculateConversionRate() {
        //given
        final CurrencyUnit fromCurrency = CurrencyUnit.of("CAD");
        final CurrencyUnit toCurrency = CurrencyUnit.of("PLN");
        givenExchangeRateInDb(fromCurrency, 1.556);
        givenExchangeRateInDb(toCurrency, 4.5557);

        //when
        final BigDecimal conversionRate = exchangeRateFacade.calculateConversionRate(fromCurrency, toCurrency);

        //then
        assertThat(conversionRate).isEqualByComparingTo("2.9278");
    }

    @Test
    void shouldFailOnUnknownCurrency() {
        //given
        final CurrencyUnit fromCurrency = CurrencyUnit.of("GBP");
        final CurrencyUnit toCurrency = CurrencyUnit.of("PLN");
        givenCurrencyIsNotInDb(fromCurrency);
        givenExchangeRateInDb(toCurrency, 4.5557);

        //when
        final Throwable exceptionThrown = catchThrowable(() -> exchangeRateFacade.calculateConversionRate(fromCurrency, toCurrency));

        //then
        assertThat(exceptionThrown).isInstanceOf(IllegalArgumentException.class);
    }


        @Test
        void shouldCreateNewExchangeRate() {
            //given
            final CurrencyUnit currency = CurrencyUnit.of("GBP");
            givenCurrencyIsNotInDb(currency);
            final UpdateExchangeRateNoticeDto updateExchangeRateNoticeDto = givenUpdateExchangeRateDto();

            //when
            exchangeRateFacade.createOrUpdateExchangeRate(currency, updateExchangeRateNoticeDto);

            //then
            assertThat(currency).satisfies(this::currencyIsInDb);
        }       
        
        @Test
        void shouldUpdateExchangeRate() {
            //given
            final CurrencyUnit currency = CurrencyUnit.of("GBP");
            givenExchangeRateInDb(currency, 4.996);
            final BigDecimal rate = valueOf(5.2321);
            final UpdateExchangeRateNoticeDto updateExchangeRateNoticeDto = new UpdateExchangeRateNoticeDto(rate, now(DEFAULT_ZONE_OFFSET));

            //when
            exchangeRateFacade.createOrUpdateExchangeRate(currency, updateExchangeRateNoticeDto);

            //then
            assertThat(currency).satisfies(c -> currencyHasRate(c, rate));
        }

    private void currencyHasRate(final CurrencyUnit currency, final BigDecimal expectedRate) {
        final Optional<BigDecimal> rate = exchangeRateRepository
                .findByCurrency(currency)
                .map(ExchangeRate::getRate);

        assertThat(rate).hasValue(expectedRate);
    }

    private void currencyIsInDb(final CurrencyUnit currency) {
        final Optional<ExchangeRate> exchangeRate = exchangeRateRepository.findByCurrency(currency);
        assertThat(exchangeRate).isNotEmpty();
    }

    private UpdateExchangeRateNoticeDto givenUpdateExchangeRateDto() {
        return new UpdateExchangeRateNoticeDto(valueOf(1.0123), now(DEFAULT_ZONE_OFFSET));
    }

    private void givenExchangeRateInDb(final CurrencyUnit currency, final double rate) {
        givenCurrencyIsNotInDb(currency);

        final UpdateExchangeRateNoticeDto rateDto = new UpdateExchangeRateNoticeDto(valueOf(rate), now(DEFAULT_ZONE_OFFSET));
        final ExchangeRate exchangeRate = ExchangeRate.create(currency, rateDto);

        exchangeRateRepository.save(exchangeRate);
    }

    private void givenCurrencyIsNotInDb(final CurrencyUnit currency) {
        exchangeRateRepository
                .findByCurrency(currency)
                .ifPresent(exchangeRateRepository::delete);
    }
    
}
